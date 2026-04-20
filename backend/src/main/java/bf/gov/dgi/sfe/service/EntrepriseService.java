package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.CertificatFiscalResponse;
import bf.gov.dgi.sfe.dto.CompteBancaireEntrepriseResponse;
import bf.gov.dgi.sfe.dto.EtablissementResponse;
import bf.gov.dgi.sfe.dto.EntrepriseRequest;
import bf.gov.dgi.sfe.dto.EntrepriseResponse;
import bf.gov.dgi.sfe.entity.CertificatFiscal;
import bf.gov.dgi.sfe.entity.CompteBancaireEntreprise;
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
    // Cree un profil entreprise complet (etablissements, certificats, comptes bancaires).
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
        entreprise.setServiceImpotRattachement(request.serviceImpotRattachement().trim());
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
            certificat.setNumeroIsf(item.numeroIsf());
            certificat.setAutoriteEmission(item.autoriteEmission());
            certificat.setDateDebutValidite(item.dateDebutValidite());
            certificat.setDateFinValidite(item.dateFinValidite());
            certificat.setActif(item.actif());
            entreprise.getCertificats().add(certificat);
        });

        request.comptesBancaires().forEach(item -> {
            CompteBancaireEntreprise compte = new CompteBancaireEntreprise();
            compte.setEntreprise(entreprise);
            compte.setReferenceCompte(item.referenceCompte().trim());
            compte.setBanque(trimToNull(item.banque()));
            compte.setActif(item.actif());
            entreprise.getComptesBancaires().add(compte);
        });

        Entreprise saved = entrepriseRepository.save(entreprise);
        return toResponse(saved);
    }

    @Transactional
    // Met a jour uniquement le logo de l'entreprise cible.
    public EntrepriseResponse updateLogo(UUID id, String logoUrl) {
        Entreprise entreprise = entrepriseRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable"));

        // Update just the logo
        entreprise.setLogoUrl(trimToNull(logoUrl));

        Entreprise saved = entrepriseRepository.save(entreprise);
        return toResponse(saved);
    }

    // Liste tous les profils entreprise.
    public List<EntrepriseResponse> list() {
        return entrepriseRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    // Retourne un profil entreprise par identifiant.
    public EntrepriseResponse get(UUID id) {
        return toResponse(entrepriseRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable")));
    }

    @Transactional(readOnly = true)
    // Retourne le profil entreprise actuellement actif.
    public EntrepriseResponse getActive() {
        Entreprise entreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc()
                .orElseThrow(() -> new IllegalArgumentException("Aucune entreprise fiscale active"));
        return toResponse(entreprise);
    }

    @Transactional(readOnly = true)
    // Verifie la disponibilite de l'identite fiscale active avant emission.
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

    // Convertit l'entite entreprise en DTO complet de reponse.
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
                cert.getNumeroIsf(),
                        cert.getAutoriteEmission(),
                        cert.getDateDebutValidite(),
                        cert.getDateFinValidite(),
                        cert.isActif()
                ))
                .toList();

        List<CompteBancaireEntrepriseResponse> comptesBancaires = e.getComptesBancaires().stream()
            .map(compte -> new CompteBancaireEntrepriseResponse(
                compte.getId(),
                compte.getReferenceCompte(),
                compte.getBanque(),
                compte.isActif()
            ))
            .toList();

        return new EntrepriseResponse(
                e.getId(),
                e.getNom(),
                e.getIfu(),
                e.getRccm(),
                e.getRegimeFiscal(),
                e.getServiceImpotRattachement(),
                e.getAdresse(),
                e.getPaysCode(),
                e.getVille(),
                e.getTelephone(),
                e.getEmail(),
                e.getLogoUrl(),
                e.getDateEffet(),
                e.isActif(),
                etablissements,
                certificats,
                comptesBancaires
        );
    }

    // Applique les validations metier obligatoires du profil fiscal.
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

        long distinctIsf = request.certificats().stream()
                .map(cert -> cert.numeroIsf().trim().toUpperCase())
                .distinct()
                .count();
        if (distinctIsf != request.certificats().size()) {
            throw new IllegalArgumentException("Les numeros ISF des certificats doivent etre uniques");
        }

        boolean hasActiveBankAccount = request.comptesBancaires().stream().anyMatch(compte -> compte.actif());
        if (!hasActiveBankAccount) {
            throw new IllegalArgumentException("Au moins une reference de compte bancaire active est obligatoire");
        }

        long distinctBankRefs = request.comptesBancaires().stream()
                .map(compte -> compte.referenceCompte().trim().toUpperCase())
                .distinct()
                .count();
        if (distinctBankRefs != request.comptesBancaires().size()) {
            throw new IllegalArgumentException("Les references de comptes bancaires doivent etre uniques");
        }
    }

    // Nettoie une chaine et retourne null si vide.
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
