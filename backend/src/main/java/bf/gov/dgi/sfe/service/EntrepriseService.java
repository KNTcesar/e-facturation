package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.CertificatFiscalResponse;
import bf.gov.dgi.sfe.dto.EtablissementResponse;
import bf.gov.dgi.sfe.dto.EntrepriseRequest;
import bf.gov.dgi.sfe.dto.EntrepriseResponse;
import bf.gov.dgi.sfe.entity.CertificatFiscal;
import bf.gov.dgi.sfe.entity.Etablissement;
import bf.gov.dgi.sfe.entity.Entreprise;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Gestion du profil legal et fiscal de l'entreprise.
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    @Transactional
    public EntrepriseResponse create(EntrepriseRequest request) {
        validateCoreFiscalProfile(request);

        if (request.actif()) {
            entrepriseRepository.findByActifTrue().forEach(existing -> existing.setActif(false));
        }

        Entreprise entreprise = new Entreprise();
        entreprise.setNom(request.nom());
        entreprise.setIfu(request.ifu());
        entreprise.setRccm(request.rccm());
        entreprise.setRegimeFiscal(request.regimeFiscal());
        entreprise.setAdresse(request.adresse());
        entreprise.setPaysCode(request.paysCode().toUpperCase());
        entreprise.setVille(request.ville());
        entreprise.setTelephone(request.telephone());
        entreprise.setEmail(request.email());
        entreprise.setLogoUrl(trimToNull(request.logoUrl()));
        entreprise.setDateEffet(request.dateEffet());
        entreprise.setActif(request.actif());

        request.etablissements().forEach(item -> {
            Etablissement etablissement = new Etablissement();
            etablissement.setEntreprise(entreprise);
            etablissement.setCode(item.code());
            etablissement.setNom(item.nom());
            etablissement.setAdresse(item.adresse());
            etablissement.setVille(item.ville());
            etablissement.setPrincipal(item.principal());
            etablissement.setActif(item.actif());
            entreprise.getEtablissements().add(etablissement);
        });

        request.certificats().forEach(item -> {
            CertificatFiscal certificat = new CertificatFiscal();
            certificat.setEntreprise(entreprise);
            certificat.setNumeroSerie(item.numeroSerie());
            certificat.setAutoriteEmission(item.autoriteEmission());
            certificat.setDateDebutValidite(item.dateDebutValidite());
            certificat.setDateFinValidite(item.dateFinValidite());
            certificat.setActif(item.actif());
            entreprise.getCertificats().add(certificat);
        });

        Entreprise saved = entrepriseRepository.save(entreprise);
        return toResponse(saved);
    }

    @Transactional
    public EntrepriseResponse updateLogo(UUID id, String logoUrl) {
        Entreprise entreprise = entrepriseRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable"));

        // Update just the logo
        entreprise.setLogoUrl(trimToNull(logoUrl));

        Entreprise saved = entrepriseRepository.save(entreprise);
        return toResponse(saved);
    }
    public List<EntrepriseResponse> list() {
        return entrepriseRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EntrepriseResponse get(UUID id) {
        return toResponse(entrepriseRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable")));
    }

    @Transactional(readOnly = true)
    public EntrepriseResponse getActive() {
        Entreprise entreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc()
                .orElseThrow(() -> new IllegalArgumentException("Aucune entreprise fiscale active"));
        return toResponse(entreprise);
    }

    @Transactional(readOnly = true)
    public void assertFiscalIdentityReady() {
        Entreprise active = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc()
                .orElseThrow(() -> new IllegalArgumentException("Profil fiscal actif manquant"));

        boolean hasActiveEtablissement = active.getEtablissements().stream().anyMatch(Etablissement::isActif);
        if (!hasActiveEtablissement) {
            throw new IllegalArgumentException("Aucun etablissement fiscal actif configure");
        }

        LocalDate now = LocalDate.now();
        boolean hasValidCertificate = active.getCertificats().stream()
                .anyMatch(cert -> cert.isActif()
                        && (cert.getDateDebutValidite().isEqual(now) || cert.getDateDebutValidite().isBefore(now))
                        && (cert.getDateFinValidite().isEqual(now) || cert.getDateFinValidite().isAfter(now)));
        if (!hasValidCertificate) {
            throw new IllegalArgumentException("Aucun certificat fiscal valide et actif configure");
        }
    }

    private EntrepriseResponse toResponse(Entreprise e) {
        List<EtablissementResponse> etablissements = e.getEtablissements().stream()
                .map(etab -> new EtablissementResponse(
                        etab.getId(),
                        etab.getCode(),
                        etab.getNom(),
                        etab.getAdresse(),
                        etab.getVille(),
                        etab.isPrincipal(),
                        etab.isActif()
                ))
                .toList();

        List<CertificatFiscalResponse> certificats = e.getCertificats().stream()
                .map(cert -> new CertificatFiscalResponse(
                        cert.getId(),
                        cert.getNumeroSerie(),
                        cert.getAutoriteEmission(),
                        cert.getDateDebutValidite(),
                        cert.getDateFinValidite(),
                        cert.isActif()
                ))
                .toList();

        return new EntrepriseResponse(
                e.getId(),
                e.getNom(),
                e.getIfu(),
                e.getRccm(),
                e.getRegimeFiscal(),
                e.getAdresse(),
                e.getPaysCode(),
                e.getVille(),
                e.getTelephone(),
                e.getEmail(),
                e.getLogoUrl(),
                e.getDateEffet(),
                e.isActif(),
                etablissements,
                certificats
        );
    }

    private void validateCoreFiscalProfile(EntrepriseRequest request) {
        boolean hasPrincipalEtablissement = request.etablissements().stream().anyMatch(item -> item.principal() && item.actif());
        if (!hasPrincipalEtablissement) {
            throw new IllegalArgumentException("Un etablissement principal actif est obligatoire");
        }

        boolean hasFutureEndCertificate = request.certificats().stream()
                .anyMatch(cert -> cert.actif() && cert.dateFinValidite().isAfter(LocalDate.now().minusDays(1)));
        if (!hasFutureEndCertificate) {
            throw new IllegalArgumentException("Au moins un certificat actif doit etre valide aujourd'hui");
        }

        boolean invalidWindow = request.certificats().stream()
                .anyMatch(cert -> cert.dateFinValidite().isBefore(cert.dateDebutValidite()));
        if (invalidWindow) {
            throw new IllegalArgumentException("Periode de validite certificat invalide");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
