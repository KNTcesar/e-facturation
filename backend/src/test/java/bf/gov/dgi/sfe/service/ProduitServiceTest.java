package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ProduitRequest;
import bf.gov.dgi.sfe.dto.ProduitResponse;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    void create_shouldMapRequestAndReturnResponse() {
        ProduitRequest request = new ProduitRequest(
                "PROD-001",
                "Service SFE",
                new BigDecimal("10000.00"),
                new BigDecimal("18.00"),
                "UNITE"
        );

        Produit saved = new Produit();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setReference(request.reference());
        saved.setDesignation(request.designation());
        saved.setPrixUnitaireHt(request.prixUnitaireHt());
        saved.setTauxTva(request.tauxTva());
        saved.setUnite(request.unite());

        when(produitRepository.save(any(Produit.class))).thenReturn(saved);

        ProduitResponse result = produitService.create(request);

        ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(captor.capture());
        Produit toPersist = captor.getValue();
        assertThat(toPersist.getReference()).isEqualTo("PROD-001");
        assertThat(toPersist.getDesignation()).isEqualTo("Service SFE");
        assertThat(toPersist.getPrixUnitaireHt()).isEqualByComparingTo("10000.00");
        assertThat(toPersist.getTauxTva()).isEqualByComparingTo("18.00");
        assertThat(toPersist.getUnite()).isEqualTo("UNITE");

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.reference()).isEqualTo("PROD-001");
        assertThat(result.designation()).isEqualTo("Service SFE");
    }

    @Test
    void list_shouldReturnMappedResponses() {
        Produit p1 = new Produit();
        p1.setId(UUID.randomUUID());
        p1.setReference("P-01");
        p1.setDesignation("Produit A");
        p1.setPrixUnitaireHt(new BigDecimal("5000.00"));
        p1.setTauxTva(new BigDecimal("18.00"));
        p1.setUnite("PCS");

        Produit p2 = new Produit();
        p2.setId(UUID.randomUUID());
        p2.setReference("P-02");
        p2.setDesignation("Produit B");
        p2.setPrixUnitaireHt(new BigDecimal("7500.00"));
        p2.setTauxTva(new BigDecimal("18.00"));
        p2.setUnite("PCS");

        when(produitRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProduitResponse> result = produitService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).reference()).isEqualTo("P-01");
        assertThat(result.get(0).designation()).isEqualTo("Produit A");
        assertThat(result.get(1).reference()).isEqualTo("P-02");
        assertThat(result.get(1).designation()).isEqualTo("Produit B");
    }
}
