package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.CreateFactureRequest;
import bf.gov.dgi.sfe.dto.FactureLigneRequest;
import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.entity.Client;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.entity.SerieFacture;
import bf.gov.dgi.sfe.entity.TransmissionSecef;
import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.repository.ClientRepository;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.ProduitRepository;
import bf.gov.dgi.sfe.repository.SerieFactureRepository;
import bf.gov.dgi.sfe.repository.TransmissionSecefRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactureServiceTest {

    @Mock
    private FactureRepository factureRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EntrepriseRepository entrepriseRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private SerieFactureRepository serieFactureRepository;
    @Mock
    private TransmissionSecefRepository transmissionSecefRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private EntrepriseService entrepriseService;

    @InjectMocks
    private FactureService factureService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldGenerateYearlyInviolableNumber() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "n/a")
        );

        UUID clientId = UUID.randomUUID();
        UUID produitId = UUID.randomUUID();
        LocalDate emissionDate = LocalDate.of(2026, 4, 10);

        Client client = new Client();
        client.setId(clientId);

        Produit produit = new Produit();
        produit.setId(produitId);
        produit.setDesignation("Prestation");
        produit.setPrixUnitaireHt(new BigDecimal("1000.00"));
        produit.setTauxTva(new BigDecimal("18.00"));

        SerieFacture serie = new SerieFacture();
        serie.setId(UUID.randomUUID());
        serie.setCode("A");
        serie.setExercice(2026);
        serie.setProchainNumero(15L);
        serie.setActive(true);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc()).thenReturn(Optional.empty());
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(serieFactureRepository.findActiveByCodeAndExerciceForUpdate("A", 2026)).thenReturn(Optional.of(serie));
        when(factureRepository.save(any(Facture.class))).thenAnswer(invocation -> {
            Facture f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            f.setDateCertification(OffsetDateTime.now());
            return f;
        });
        when(transmissionSecefRepository.save(any(TransmissionSecef.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateFactureRequest request = new CreateFactureRequest(
                clientId,
                "A",
                emissionDate,
            "MARCHE-TEST-001",
            "Test prestation",
            emissionDate,
            emissionDate,
            emissionDate.plusDays(30),
                List.of(new FactureLigneRequest(produitId, new BigDecimal("2")))
        );

        FactureResponse response = factureService.create(request);

        assertThat(response.numero()).isEqualTo("A-2026-00000015");
        assertThat(response.exercice()).isEqualTo(2026);
        assertThat(response.statut()).isEqualTo(StatutFacture.CERTIFIEE);
        assertThat(serie.getProchainNumero()).isEqualTo(16L);

        ArgumentCaptor<Facture> factureCaptor = ArgumentCaptor.forClass(Facture.class);
        verify(factureRepository).save(factureCaptor.capture());
        assertThat(factureCaptor.getValue().getNumero()).isEqualTo("A-2026-00000015");
    }

    @Test
    void annuler_shouldRejectAlreadyCancelledInvoice() {
        Facture facture = new Facture();
        facture.setId(UUID.randomUUID());
        facture.setStatut(StatutFacture.ANNULEE);

        when(factureRepository.findById(facture.getId())).thenReturn(Optional.of(facture));

        assertThatThrownBy(() -> factureService.annuler(facture.getId(), "Erreur"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deja annulee");
    }
}
