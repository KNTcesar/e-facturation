package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.CreateFactureRequest;
import bf.gov.dgi.sfe.dto.FactureClientInfoResponse;
import bf.gov.dgi.sfe.dto.FactureEntrepriseInfoResponse;
import bf.gov.dgi.sfe.dto.FactureLigneRequest;
import bf.gov.dgi.sfe.dto.FactureLigneResponse;
import bf.gov.dgi.sfe.dto.FactureProduitInfoResponse;
import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.entity.Client;
import bf.gov.dgi.sfe.entity.Entreprise;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.LigneFacture;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.entity.SerieFacture;
import bf.gov.dgi.sfe.entity.TransmissionSecef;
import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.StatutTransmission;
import bf.gov.dgi.sfe.repository.ClientRepository;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.ProduitRepository;
import bf.gov.dgi.sfe.repository.SerieFactureRepository;
import bf.gov.dgi.sfe.repository.TransmissionSecefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// Orchestration metier de la creation et de la certification d'une facture.
public class FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final ProduitRepository produitRepository;
    private final SerieFactureRepository serieFactureRepository;
    private final TransmissionSecefRepository transmissionSecefRepository;
    private final AuditService auditService;
    private final EntrepriseService entrepriseService;

    @Transactional
    public FactureResponse create(CreateFactureRequest request) {
        entrepriseService.assertFiscalIdentityReady();
        Entreprise activeEntreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc().orElse(null);

        // 1) Verifier les referentiels obligatoires.
        UUID clientId = Objects.requireNonNull(request.clientId(), "clientId is required");
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));

        if (request.dateFinExecution().isBefore(request.dateDebutExecution())) {
            throw new IllegalArgumentException("La date de fin d'execution doit etre posterieure a la date de debut");
        }
        if (request.dateMarche().isAfter(request.dateFinExecution())) {
            throw new IllegalArgumentException("La date du marche ne peut pas etre apres la fin d'execution");
        }

        int exercice = request.dateEmission().getYear();
        SerieFacture serie = serieFactureRepository.findActiveByCodeAndExerciceForUpdate(request.serieCode(), exercice)
            .orElseThrow(() -> new IllegalArgumentException("Serie active introuvable pour l'exercice " + exercice));

        // 2) Reserver un numero sequentiel unique.
        long number = serie.getProchainNumero();
        serie.setProchainNumero(number + 1);
        String numero = serie.getCode() + "-" + exercice + "-" + String.format("%08d", number);

        Facture facture = new Facture();
        facture.setNumero(numero);
        facture.setDateEmission(request.dateEmission());
        facture.setExercice(exercice);
        facture.setClient(client);
        facture.setSerieFacture(serie);
        facture.setStatut(StatutFacture.CERTIFIEE);
        facture.setDateCertification(OffsetDateTime.now());
        facture.setReferenceMarche(requireNonBlank(request.referenceMarche(), "La reference marche est obligatoire"));
        facture.setObjetMarche(requireNonBlank(request.objetMarche(), "L'objet marche est obligatoire"));
        facture.setDateMarche(request.dateMarche());
        facture.setDateDebutExecution(request.dateDebutExecution());
        facture.setDateFinExecution(request.dateFinExecution());

        // 3) Calculer les totaux a partir des lignes.
        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        BigDecimal totalTtc = BigDecimal.ZERO;

        for (FactureLigneRequest lineReq : request.lignes()) {
            UUID produitId = Objects.requireNonNull(lineReq.produitId(), "produitId is required");
            Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + produitId));
            BigDecimal montantHt = produit.getPrixUnitaireHt().multiply(lineReq.quantite());
            BigDecimal montantTva = montantHt.multiply(produit.getTauxTva()).divide(new BigDecimal("100"));
            BigDecimal montantTtc = montantHt.add(montantTva);

            LigneFacture line = new LigneFacture();
            line.setFacture(facture);
            line.setProduit(produit);
            line.setDescription(produit.getDesignation());
            line.setQuantite(lineReq.quantite());
            line.setPrixUnitaireHt(produit.getPrixUnitaireHt());
            line.setTauxTva(produit.getTauxTva());
            line.setMontantHt(montantHt);
            line.setMontantTva(montantTva);
            line.setMontantTtc(montantTtc);
            facture.getLignes().add(line);

            totalHt = totalHt.add(montantHt);
            totalTva = totalTva.add(montantTva);
            totalTtc = totalTtc.add(montantTtc);
        }

        facture.setTotalHt(totalHt);
        facture.setTotalTva(totalTva);
        facture.setTotalTtc(totalTtc);

        // 4) Generer le code d'authentification et la charge QR.
        String authCode = generateAuthCode(numero, totalTtc, clientId);
        facture.setCodeAuthentification(authCode);
        facture.setQrPayload(buildQrPayload(facture, activeEntreprise, "EN_ATTENTE"));
        
        // Enregistrer le logo de l'entreprise au moment de la faction (snapshot)
        facture.setLogoUrl(activeEntreprise != null ? activeEntreprise.getLogoUrl() : null);

        facture = factureRepository.save(facture);

        // 5) Creer une trace de transmission fiscale initiale.
        TransmissionSecef tx = new TransmissionSecef();
        tx.setFacture(facture);
        tx.setFormatPayload("JSON");
        tx.setPayloadHash(generateHash(facture.getQrPayload()));
        tx.setStatut(StatutTransmission.PENDING);
        tx.setDateEnvoi(OffsetDateTime.now());
        tx.setRetryCount(0);
        transmissionSecefRepository.save(tx);

        // 6) Journaliser l'action pour l'audit immuable.
        String acteur = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.trace("CREATE_FACTURE", "Facture", facture.getId(), acteur);

        return toResponse(facture);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> list() {
        return factureRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FactureResponse get(UUID id) {
        // Retourne la facture avec ses totaux et son statut.
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));
        return toResponse(facture);
    }

    @Transactional
    public FactureResponse annuler(UUID id, String motif) {
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));

        if (facture.getStatut() == StatutFacture.BROUILLON) {
            throw new IllegalArgumentException("Une facture brouillon doit etre supprimee avant certification");
        }
        if (facture.getStatut() == StatutFacture.ANNULEE) {
            throw new IllegalArgumentException("Cette facture est deja annulee");
        }

        facture.setStatut(StatutFacture.ANNULEE);
        facture.setMotifAnnulation(motif);
        Facture saved = factureRepository.save(facture);

        String acteur = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.trace("ANNULER_FACTURE", "Facture", saved.getId(), acteur);

        return toResponse(saved);
    }

    private FactureResponse toResponse(Facture facture) {
        Entreprise activeEntreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc().orElse(null);
        String validationDgi = resolveValidationDgi(facture.getStatut());
        String resolvedLogoUrl = trimToNull(facture.getLogoUrl());
        if (resolvedLogoUrl == null && activeEntreprise != null) {
            resolvedLogoUrl = trimToNull(activeEntreprise.getLogoUrl());
        }

        FactureEntrepriseInfoResponse entreprise = activeEntreprise == null ? null : new FactureEntrepriseInfoResponse(
            activeEntreprise.getId(),
            activeEntreprise.getNom(),
            activeEntreprise.getIfu(),
            activeEntreprise.getRccm(),
            activeEntreprise.getAdresse(),
            activeEntreprise.getVille(),
            activeEntreprise.getPaysCode(),
            activeEntreprise.getTelephone(),
            activeEntreprise.getEmail(),
            resolvedLogoUrl
        );

        FactureClientInfoResponse client = new FactureClientInfoResponse(
            facture.getClient().getId(),
            facture.getClient().getNom(),
            facture.getClient().getIfu(),
            facture.getClient().getAdresse(),
            facture.getClient().getTelephone(),
            facture.getClient().getEmail()
        );

        List<FactureLigneResponse> lignes = facture.getLignes().stream()
            .map(ligne -> new FactureLigneResponse(
                ligne.getId(),
                ligne.getDescription(),
                ligne.getQuantite(),
                ligne.getPrixUnitaireHt(),
                ligne.getTauxTva(),
                ligne.getMontantHt(),
                ligne.getMontantTva(),
                ligne.getMontantTtc(),
                new FactureProduitInfoResponse(
                    ligne.getProduit().getId(),
                    ligne.getProduit().getReference(),
                    ligne.getProduit().getDesignation(),
                    ligne.getProduit().getUnite()
                )
            ))
            .toList();

        return new FactureResponse(
                facture.getId(),
                facture.getNumero(),
                facture.getDateEmission(),
                facture.getStatut(),
                facture.getExercice(),
                facture.getTotalHt(),
                facture.getTotalTva(),
                facture.getTotalTtc(),
                facture.getCodeAuthentification(),
                facture.getQrPayload(),
                facture.getMotifAnnulation(),
                facture.getReferenceMarche(),
                facture.getObjetMarche(),
                facture.getDateMarche(),
                facture.getDateDebutExecution(),
                facture.getDateFinExecution(),
                validationDgi,
                entreprise,
                client,
                lignes
        );
    }

    private String resolveValidationDgi(StatutFacture statut) {
        if (statut == StatutFacture.ACCEPTEE) {
            return "VALIDEE";
        }
        if (statut == StatutFacture.REJETEE) {
            return "REJETEE";
        }
        return "EN_ATTENTE";
    }

    private String buildQrPayload(Facture facture, Entreprise entreprise, String validationDgi) {
        String entrepriseNom = entreprise != null ? nonNull(entreprise.getNom()) : "";
        String entrepriseIfu = entreprise != null ? nonNull(entreprise.getIfu()) : "";
        String clientNom = facture.getClient() != null ? nonNull(facture.getClient().getNom()) : "";
        String clientIfu = facture.getClient() != null ? nonNull(facture.getClient().getIfu()) : "";

        String lignesPayload = facture.getLignes().stream()
                .map(l -> "%s,%s,%s,%s,%s".formatted(
                        sanitize(nonNull(l.getProduit().getReference())),
                        sanitize(nonNull(l.getProduit().getDesignation())),
                        sanitize(nonNull(l.getProduit().getUnite())),
                        l.getQuantite(),
                        l.getMontantTtc()
                ))
                .reduce((a, b) -> a + ";" + b)
                .orElse("");

        return "NUM=%s|DATE=%s|TOTAL_TTC=%s|AUTH=%s|DGI=%s|MARCHE_REF=%s|MARCHE_OBJET=%s|MARCHE_DATE=%s|EXEC_DEBUT=%s|EXEC_FIN=%s|ENT_NOM=%s|ENT_IFU=%s|CLI_NOM=%s|CLI_IFU=%s|LIGNES=%s"
                .formatted(
                        sanitize(nonNull(facture.getNumero())),
                        facture.getDateEmission(),
                        facture.getTotalTtc(),
                        sanitize(nonNull(facture.getCodeAuthentification())),
                        sanitize(nonNull(validationDgi)),
                        sanitize(nonNull(facture.getReferenceMarche())),
                        sanitize(nonNull(facture.getObjetMarche())),
                        facture.getDateMarche() != null ? facture.getDateMarche() : "",
                        facture.getDateDebutExecution() != null ? facture.getDateDebutExecution() : "",
                        facture.getDateFinExecution() != null ? facture.getDateFinExecution() : "",
                        sanitize(entrepriseNom),
                        sanitize(entrepriseIfu),
                        sanitize(clientNom),
                        sanitize(clientIfu),
                        sanitize(lignesPayload)
                );
    }

    private String sanitize(String value) {
        return value.replace("|", " ").replace(";", " ").replace(",", " ");
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String requireNonBlank(String value, String message) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String generateAuthCode(String numero, BigDecimal ttc, UUID clientId) {
        return generateHash(numero + "|" + ttc + "|" + clientId).substring(0, 16).toUpperCase();
    }

    private String generateHash(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de hash", ex);
        }
    }
}
