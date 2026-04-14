package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.CertificatFiscalRequest;
import bf.gov.dgi.sfe.dto.EtablissementRequest;
import bf.gov.dgi.sfe.dto.EntrepriseRequest;
import bf.gov.dgi.sfe.dto.EntrepriseResponse;
import bf.gov.dgi.sfe.entity.CertificatFiscal;
import bf.gov.dgi.sfe.entity.Etablissement;
import bf.gov.dgi.sfe.entity.Entreprise;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private EntrepriseService entrepriseService;

    @Test
    @SuppressWarnings("null")
    void create_shouldSaveActiveFiscalProfile() {
        EntrepriseRequest request = buildValidRequest();

        Entreprise saved = new Entreprise();
        saved.setId(UUID.randomUUID());
        saved.setNom(request.nom());
        saved.setIfu(request.ifu());
        saved.setRccm(request.rccm());
        saved.setRegimeFiscal(request.regimeFiscal());
        saved.setAdresse(request.adresse());
        saved.setPaysCode(request.paysCode());
        saved.setVille(request.ville());
        saved.setTelephone(request.telephone());
        saved.setEmail(request.email());
        saved.setLogoUrl(request.logoUrl());
        saved.setDateEffet(request.dateEffet());
        saved.setActif(true);

        Etablissement etablissement = new Etablissement();
        etablissement.setId(UUID.randomUUID());
        etablissement.setEntreprise(saved);
        etablissement.setCode("HQ");
        etablissement.setNom("Siege");
        etablissement.setAdresse("Ouaga 2000");
        etablissement.setVille("Ouagadougou");
        etablissement.setPrincipal(true);
        etablissement.setActif(true);
        saved.getEtablissements().add(etablissement);

        CertificatFiscal cert = new CertificatFiscal();
        cert.setId(UUID.randomUUID());
        cert.setEntreprise(saved);
        cert.setNumeroSerie("CERT-001");
        cert.setAutoriteEmission("DGI");
        cert.setDateDebutValidite(LocalDate.now().minusDays(10));
        cert.setDateFinValidite(LocalDate.now().plusDays(30));
        cert.setActif(true);
        saved.getCertificats().add(cert);

        when(entrepriseRepository.findByActifTrue()).thenReturn(List.of());
        when(entrepriseRepository.save(any(Entreprise.class))).thenReturn(saved);

        EntrepriseResponse result = entrepriseService.create(request);

        ArgumentCaptor<Entreprise> captor = ArgumentCaptor.forClass(Entreprise.class);
        verify(entrepriseRepository).save(captor.capture());
        Entreprise toPersist = captor.getValue();
        assertThat(toPersist.getNom()).isEqualTo("Entreprise Test");
        assertThat(toPersist.getPaysCode()).isEqualTo("BF");
        assertThat(toPersist.getEtablissements()).hasSize(1);
        assertThat(toPersist.getCertificats()).hasSize(1);

        assertThat(result.nom()).isEqualTo("Entreprise Test");
        assertThat(result.logoUrl()).isEqualTo("https://cdn.example/logo.png");
        assertThat(result.etablissements()).hasSize(1);
        assertThat(result.certificats()).hasSize(1);
    }

    @Test
    @SuppressWarnings("null")
    void create_shouldRejectWhenNoPrincipalActiveEtablissement() {
        EntrepriseRequest request = new EntrepriseRequest(
                "Entreprise Test",
                "IFU-0001",
                "RCCM-0001",
                "REEL_NORMAL",
                "Ouaga 2000",
                "BF",
                "Ouagadougou",
                "+22670000000",
                "fiscal@test.bf",
                "https://cdn.example/logo.png",
                LocalDate.now(),
                true,
                List.of(new EtablissementRequest("BR1", "Agence", "Zone 1", "Bobo", false, true)),
                List.of(new CertificatFiscalRequest("CERT-001", "DGI", LocalDate.now().minusDays(2), LocalDate.now().plusDays(20), true))
        );

        assertThatThrownBy(() -> entrepriseService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("etablissement principal actif");

        verify(entrepriseRepository, never()).save(any(Entreprise.class));
    }

    @Test
    void assertFiscalIdentityReady_shouldRejectWhenNoActiveProfile() {
        when(entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrepriseService.assertFiscalIdentityReady())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profil fiscal actif manquant");
    }

    private EntrepriseRequest buildValidRequest() {
        return new EntrepriseRequest(
                "Entreprise Test",
                "IFU-0001",
                "RCCM-0001",
                "REEL_NORMAL",
                "Ouaga 2000",
                "BF",
                "Ouagadougou",
                "+22670000000",
                "fiscal@test.bf",
                "https://cdn.example/logo.png",
                LocalDate.now(),
                true,
                List.of(new EtablissementRequest("HQ", "Siege", "Ouaga 2000", "Ouagadougou", true, true)),
                List.of(new CertificatFiscalRequest("CERT-001", "DGI", LocalDate.now().minusDays(5), LocalDate.now().plusDays(365), true))
        );
    }
}
