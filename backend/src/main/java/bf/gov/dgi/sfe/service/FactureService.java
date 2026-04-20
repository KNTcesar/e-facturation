package bf.gov.dgi.sfe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import bf.gov.dgi.sfe.dto.CreateFactureRequest;
import bf.gov.dgi.sfe.dto.FactureCommentaireRequest;
import bf.gov.dgi.sfe.dto.FactureCommentaireResponse;
import bf.gov.dgi.sfe.dto.FactureClientInfoResponse;
import bf.gov.dgi.sfe.dto.FactureDuplicataResponse;
import bf.gov.dgi.sfe.dto.FactureEntrepriseInfoResponse;
import bf.gov.dgi.sfe.dto.FactureLigneRequest;
import bf.gov.dgi.sfe.dto.FactureLigneResponse;
import bf.gov.dgi.sfe.dto.FacturePaiementRequest;
import bf.gov.dgi.sfe.dto.FacturePaiementResponse;
import bf.gov.dgi.sfe.dto.FactureProduitInfoResponse;
import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.entity.Client;
import bf.gov.dgi.sfe.entity.Entreprise;
import bf.gov.dgi.sfe.entity.FactureDuplicata;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.FactureCommentaire;
import bf.gov.dgi.sfe.entity.FacturePaiement;
import bf.gov.dgi.sfe.entity.LigneFacture;
import bf.gov.dgi.sfe.entity.ParamLigneCommentaire;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.entity.SerieFacture;
import bf.gov.dgi.sfe.entity.TransmissionSecef;
import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;
import bf.gov.dgi.sfe.enums.GroupePsvb;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import bf.gov.dgi.sfe.enums.ModePaiement;
import bf.gov.dgi.sfe.enums.NatureFactureAvoir;
import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.StatutTransmission;
import bf.gov.dgi.sfe.enums.TypeArticle;
import bf.gov.dgi.sfe.enums.TypeFacture;
import bf.gov.dgi.sfe.repository.ClientRepository;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import bf.gov.dgi.sfe.repository.FactureDuplicataRepository;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.ParamLigneCommentaireRepository;
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
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
// Orchestration metier de la creation et de la certification d'une facture.
public class FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final ProduitRepository produitRepository;
    private final ParamLigneCommentaireRepository paramLigneCommentaireRepository;
    private final SerieFactureRepository serieFactureRepository;
    private final FactureDuplicataRepository factureDuplicataRepository;
    private final TransmissionSecefRepository transmissionSecefRepository;
    private final AuditService auditService;
    private final EntrepriseService entrepriseService;
    private final MouvementStockService mouvementStockService;
    private final McfSecurityService mcfSecurityService;
    private final JournalElectroniqueService journalElectroniqueService;
    private final QrCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @Transactional
    // Cree, calcule, certifie et journalise une facture complete (lignes, paiements, QR, securite).
    public FactureResponse create(CreateFactureRequest request) {
        entrepriseService.assertFiscalIdentityReady();
        Entreprise activeEntreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc().orElse(null);

        // 1) Verifier les referentiels obligatoires.
        UUID clientId = Objects.requireNonNull(request.clientId(), "clientId is required");
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));

        validateCommentaires(request.commentaires());

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
        TypeFacture typeFacture = request.typeFacture() == null ? TypeFacture.FV : request.typeFacture();
        ModePrixArticle modePrixUnitaire = request.modePrixUnitaire() == null ? ModePrixArticle.HT : request.modePrixUnitaire();
        facture.setTypeFacture(typeFacture);
        facture.setModePrixUnitaire(modePrixUnitaire);
        if (typeFacture == TypeFacture.FA || typeFacture == TypeFacture.EA) {
            if (request.natureFactureAvoir() == null) {
                throw new IllegalArgumentException("La nature de facture d'avoir est obligatoire pour FA et EA");
            }
            facture.setNatureFactureAvoir(request.natureFactureAvoir());
            String referenceOriginale = trimToNull(request.referenceFactureOriginale());
            if (request.natureFactureAvoir() == NatureFactureAvoir.IRRR) {
                if (!"RRR".equalsIgnoreCase(nonNull(referenceOriginale))) {
                    throw new IllegalArgumentException("Pour une facture d'avoir IRRR, la reference de facture originale doit etre 'RRR'");
                }
                facture.setReferenceFactureOriginale("RRR");
            } else {
                facture.setReferenceFactureOriginale(referenceOriginale);
            }
        } else {
            facture.setNatureFactureAvoir(null);
            facture.setReferenceFactureOriginale(null);
        }
        facture.setStatut(StatutFacture.CERTIFIEE);
        facture.setDateCertification(OffsetDateTime.now());
        facture.setReferenceMarche(trimToNull(request.referenceMarche()));
        facture.setObjetMarche(trimToNull(request.objetMarche()));
        facture.setDateMarche(request.dateMarche());
        facture.setDateDebutExecution(request.dateDebutExecution());
        facture.setDateFinExecution(request.dateFinExecution());
        facture.setGroupePsvb(request.groupePsvb());

        // 3) Calculer les totaux a partir des lignes.
        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        BigDecimal totalTtc = BigDecimal.ZERO;
        boolean requiresMarketInfo = false;

        String acteur = resolveActor();
        for (FactureLigneRequest lineReq : request.lignes()) {
            UUID produitId = Objects.requireNonNull(lineReq.produitId(), "produitId is required");
            Produit produit = produitRepository.findByIdForUpdate(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + produitId));
            if (produit.getTypeArticle() == TypeArticle.LOCSER) {
                requiresMarketInfo = true;
            }

            BigDecimal quantite = lineReq.quantite().setScale(3, RoundingMode.HALF_UP);
            if (produit.getTypeArticle() != TypeArticle.LOCSER) {
                BigDecimal stockDisponible = produit.getQuantiteDisponible() == null
                    ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                    : produit.getQuantiteDisponible().setScale(3, RoundingMode.HALF_UP);
                if (stockDisponible.compareTo(quantite) < 0) {
                    throw new IllegalArgumentException("Stock insuffisant pour le produit " + produit.getReference());
                }
                BigDecimal stockApres = stockDisponible.subtract(quantite).setScale(3, RoundingMode.HALF_UP);
                produit.setQuantiteDisponible(stockApres);
                mouvementStockService.recordSortieFacture(
                    produit,
                    quantite,
                    stockDisponible,
                    stockApres,
                    numero,
                    acteur
                );
            }

            BigDecimal montantHt;
            BigDecimal montantTtc;
            BigDecimal taxeSpecifiqueUnitaire = produit.getTaxeSpecifiqueUnitaire() == null ? BigDecimal.ZERO : produit.getTaxeSpecifiqueUnitaire();
            BigDecimal montantTaxeSpecifique = taxeSpecifiqueUnitaire.multiply(quantite).setScale(2, RoundingMode.HALF_UP);
            BigDecimal baseTaxableTva;
            BigDecimal montantTva;

            if (facture.getModePrixUnitaire() == ModePrixArticle.TTC) {
                montantTtc = produit.getPrixUnitaireTtc().multiply(quantite).setScale(2, RoundingMode.HALF_UP);
                BigDecimal taux = produit.getTauxTva();
                BigDecimal coefficientTva = BigDecimal.ONE.add(taux.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
                baseTaxableTva = montantTtc.divide(coefficientTva, 2, RoundingMode.HALF_UP);
                montantTva = montantTtc.subtract(baseTaxableTva).setScale(2, RoundingMode.HALF_UP);
                montantHt = baseTaxableTva.subtract(montantTaxeSpecifique).setScale(2, RoundingMode.HALF_UP);
            } else {
                montantHt = produit.getPrixUnitaireHt().multiply(quantite).setScale(2, RoundingMode.HALF_UP);
                // Le HT est considere sans taxe specifique; la base taxable TVA est augmentee de la taxe specifique.
                baseTaxableTva = montantHt.add(montantTaxeSpecifique).setScale(2, RoundingMode.HALF_UP);
                montantTva = roundTaxUp(baseTaxableTva, produit.getTauxTva());
                montantTtc = baseTaxableTva.add(montantTva).setScale(2, RoundingMode.HALF_UP);
            }

            if (montantHt.compareTo(BigDecimal.ZERO) <= 0 || montantTtc.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant de chaque article doit etre strictement positif");
            }

            if (baseTaxableTva.add(montantTva).setScale(2, RoundingMode.HALF_UP).compareTo(montantTtc.setScale(2, RoundingMode.HALF_UP)) != 0) {
                montantTva = montantTtc.subtract(baseTaxableTva).setScale(2, RoundingMode.HALF_UP);
            }

            LigneFacture line = new LigneFacture();
            line.setFacture(facture);
            line.setProduit(produit);
            line.setDescription(produit.getDesignation());
            line.setQuantite(quantite);
            line.setPrixUnitaireHt(produit.getPrixUnitaireHt());
            line.setTauxTva(produit.getTauxTva());
            line.setGroupeTaxation(produit.getGroupeTaxation());
            line.setMontantTaxeSpecifique(montantTaxeSpecifique);
            line.setBaseTaxableTva(baseTaxableTva);
            line.setMontantHt(montantHt);
            line.setMontantTva(montantTva);
            line.setMontantTtc(montantTtc);
            facture.getLignes().add(line);

            totalHt = totalHt.add(montantHt);
            totalTva = totalTva.add(montantTva);
            totalTtc = totalTtc.add(montantTtc);
        }

        if (requiresMarketInfo) {
            facture.setReferenceMarche(requireNonBlank(request.referenceMarche(), "La reference marche est obligatoire pour les services"));
            facture.setObjetMarche(requireNonBlank(request.objetMarche(), "L'objet marche est obligatoire pour les services"));
            if (request.dateMarche() == null || request.dateDebutExecution() == null || request.dateFinExecution() == null) {
                throw new IllegalArgumentException("Les dates du marche sont obligatoires pour les services");
            }
            if (request.dateFinExecution().isBefore(request.dateDebutExecution())) {
                throw new IllegalArgumentException("La date de fin d'execution doit etre posterieure a la date de debut");
            }
            if (request.dateMarche().isAfter(request.dateFinExecution())) {
                throw new IllegalArgumentException("La date du marche ne peut pas etre apres la fin d'execution");
            }
        } else {
            facture.setReferenceMarche(trimToNull(request.referenceMarche()));
            facture.setObjetMarche(trimToNull(request.objetMarche()));
            facture.setDateMarche(request.dateMarche());
            facture.setDateDebutExecution(request.dateDebutExecution());
            facture.setDateFinExecution(request.dateFinExecution());
        }

        facture.setTotalHt(totalHt);
        facture.setTotalTva(totalTva);
        facture.setTotalTtc(totalTtc);
        if (normalizeMoney(totalTtc).compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant total de la facture doit etre strictement positif");
        }
        if (facture.getGroupePsvb() == null) {
            facture.setTauxPsvb(BigDecimal.ZERO);
            facture.setMontantPsvb(BigDecimal.ZERO);
        } else {
            BigDecimal taux = facture.getGroupePsvb().taux();
            BigDecimal montant = totalTtc.multiply(taux).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            facture.setTauxPsvb(taux);
            facture.setMontantPsvb(montant);
        }

        // 3.b) Ventiler les paiements et forcer la coherence montant facture/paiements.
        List<FacturePaiementRequest> paiementsRequest = request.paiements();
        if (paiementsRequest == null || paiementsRequest.isEmpty()) {
            FacturePaiement paiement = new FacturePaiement();
            paiement.setFacture(facture);
            paiement.setModePaiement(ModePaiement.ESPECES);
            paiement.setMontant(totalTtc);
            paiement.setReferencePaiement(null);
            facture.getPaiements().add(paiement);
        } else {
            BigDecimal totalPaiements = BigDecimal.ZERO;
            for (FacturePaiementRequest paiementReq : paiementsRequest) {
                BigDecimal montant = normalizeMoney(paiementReq.montant());
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Le montant d'un paiement doit etre strictement positif");
                }

                FacturePaiement paiement = new FacturePaiement();
                paiement.setFacture(facture);
                paiement.setModePaiement(paiementReq.modePaiement());
                paiement.setMontant(montant);
                paiement.setReferencePaiement(trimToNull(paiementReq.referencePaiement()));
                facture.getPaiements().add(paiement);

                totalPaiements = totalPaiements.add(montant);
            }

            if (normalizeMoney(totalPaiements).compareTo(normalizeMoney(totalTtc)) != 0) {
                throw new IllegalArgumentException("Le total des paiements doit etre egal au montant total TTC de la facture");
            }
        }

        // 3.c) Construire les lignes de commentaires A..H (max 8) depuis request ou parametres.
        attachCommentaires(facture, request.commentaires());

        // 4) Generer le code d'authentification et la charge QR.
        McfSecurityService.McfSecurityElements securityElements = mcfSecurityService.obtainSecurityElements(numero, totalTtc, clientId);
        facture.setCodeAuthentification(securityElements.codeAuthentification());
        String qrPayload = buildQrPayload(facture, activeEntreprise, "EN_ATTENTE");
        validateQrPayloadStructure(qrPayload);
        facture.setQrPayload(qrPayload);
        
        // Enregistrer le logo de l'entreprise au moment de la faction (snapshot)
        facture.setLogoUrl(activeEntreprise != null ? activeEntreprise.getLogoUrl() : null);

        facture = factureRepository.save(facture);

        // 5) Creer une trace de transmission fiscale initiale.
        TransmissionSecef tx = new TransmissionSecef();
        tx.setFacture(facture);
        tx.setFormatPayload("JSON");
        tx.setPayloadData(facture.getQrPayload());
        tx.setPayloadHash(generateHash(tx.getPayloadData()));
        tx.setStatut(StatutTransmission.PENDING);
        tx.setDateEnvoi(OffsetDateTime.now());
        tx.setRetryCount(0);
        transmissionSecefRepository.save(tx);

        // 6) Journaliser l'action pour l'audit immuable.
        auditService.trace("CREATE_FACTURE", "Facture", facture.getId(), acteur);

        FactureResponse response = toResponse(facture);
        journalElectroniqueService.recordFacture(response);
        return response;
    }

    @Transactional(readOnly = true)
    // Liste les factures existantes sous forme de reponse metier.
    public List<FactureResponse> list() {
        return factureRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    // Recupere une facture par identifiant.
    public FactureResponse get(UUID id) {
        // Retourne la facture avec ses totaux et son statut.
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));
        return toResponse(facture);
    }

    @Transactional(readOnly = true)
    // Genere une image PNG du QR code de la facture.
    public byte[] getQrCodePng(UUID id, int size) {
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));
        return qrCodeService.generateScannablePng(facture.getQrPayload(), size);
    }

    @Transactional
    // Annule une facture certifiee et trace l'operation dans l'audit.
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

    @Transactional
    // Enregistre un duplicata immuable de la facture et retourne son snapshot.
    public FactureResponse duplicata(UUID id) {
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));

        FactureResponse snapshot = toResponse(facture);
        String snapshotJson = toSnapshotJson(snapshot);

        FactureDuplicata duplicata = new FactureDuplicata();
        duplicata.setFacture(facture);
        duplicata.setNumeroFacture(facture.getNumero());
        duplicata.setDateFacture(facture.getDateEmission());
        duplicata.setDemandeur(resolveActor());
        duplicata.setMotif("Duplicata");
        duplicata.setSnapshotJson(snapshotJson);
        duplicata.setEmpreinteSnapshot(generateHash(snapshotJson));
        factureDuplicataRepository.save(duplicata);

        auditService.trace(
                "GENERATE_DUPLICATA",
                "Facture",
                facture.getId(),
                duplicata.getDemandeur(),
                null,
                duplicata.getEmpreinteSnapshot(),
                "Duplicata genere pour la facture " + facture.getNumero()
        );

        return snapshot;
    }

    @Transactional(readOnly = true)
    // Retourne l'historique des duplicatas generes pour une facture.
    public List<FactureDuplicataResponse> listDuplicatas(UUID id) {
        Facture facture = factureRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable"));

        return factureDuplicataRepository.findByFactureIdOrderByCreatedAtDesc(facture.getId()).stream()
                .map(this::toDuplicataResponse)
                .toList();
    }

    // Construit la reponse API complete d'une facture a partir des entites persistantes.
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
            facture.getClient().getRccm(),
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
                ligne.getGroupeTaxation(),
                ligne.getMontantTaxeSpecifique(),
                ligne.getBaseTaxableTva(),
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

            List<FacturePaiementResponse> paiements = facture.getPaiements() == null
                ? new ArrayList<>()
                : facture.getPaiements().stream()
                .map(p -> new FacturePaiementResponse(
                    p.getId(),
                    p.getModePaiement(),
                    p.getMontant(),
                    p.getReferencePaiement()
                ))
                .toList();

            List<FactureCommentaireResponse> commentaires = facture.getCommentaires() == null
                ? new ArrayList<>()
                : facture.getCommentaires().stream()
                .map(c -> new FactureCommentaireResponse(
                    c.getId(),
                    c.getCode(),
                    c.getEtiquette(),
                    c.getContenu(),
                    c.getOrdreAffichage()
                ))
                .toList();

        return new FactureResponse(
                facture.getId(),
                facture.getNumero(),
                facture.getDateEmission(),
                facture.getTypeFacture(),
                facture.getModePrixUnitaire(),
                facture.getNatureFactureAvoir(),
                facture.getReferenceFactureOriginale(),
                facture.getStatut(),
                facture.getExercice(),
                facture.getTotalHt(),
                facture.getTotalTva(),
                facture.getTotalTtc(),
                facture.getGroupePsvb(),
                facture.getTauxPsvb(),
                facture.getMontantPsvb(),
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
                commentaires,
                paiements,
                lignes
        );
    }

    // Traduit le statut facture en statut de validation metier pour l'affichage.
    private String resolveValidationDgi(StatutFacture statut) {
        if (statut == StatutFacture.ACCEPTEE) {
            return "VALIDEE";
        }
        if (statut == StatutFacture.REJETEE) {
            return "REJETEE";
        }
        return "EN_ATTENTE";
    }

    // Convertit une entite de duplicata en DTO de reponse.
    private FactureDuplicataResponse toDuplicataResponse(FactureDuplicata duplicata) {
        return new FactureDuplicataResponse(
                duplicata.getId(),
                duplicata.getFacture().getId(),
                duplicata.getNumeroFacture(),
                duplicata.getDateFacture(),
                duplicata.getDemandeur(),
                duplicata.getMotif(),
                duplicata.getEmpreinteSnapshot(),
                duplicata.getCreatedAt()
        );
    }

    // Construit la charge utile QR a partir des informations fiscales de la facture.
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

    // Nettoie les separateurs reserves pour garantir un format QR parseable.
    private String sanitize(String value) {
        return value.replace("|", " ").replace(";", " ").replace(",", " ");
    }

    // Controle la presence des cles obligatoires dans la charge QR.
    private void validateQrPayloadStructure(String payload) {
        String[] tokens = payload.split("\\|");
        Set<String> keys = new HashSet<>();
        for (String token : tokens) {
            int idx = token.indexOf('=');
            if (idx <= 0) {
                throw new IllegalStateException("Format de payload QR invalide");
            }
            keys.add(token.substring(0, idx));
        }

        List<String> required = List.of(
                "NUM", "DATE", "TOTAL_TTC", "AUTH", "DGI", "MARCHE_REF", "MARCHE_OBJET",
                "MARCHE_DATE", "EXEC_DEBUT", "EXEC_FIN", "ENT_NOM", "ENT_IFU", "CLI_NOM",
                "CLI_IFU", "LIGNES"
        );

        for (String key : required) {
            if (!keys.contains(key)) {
                throw new IllegalStateException("Le payload QR ne contient pas le champ obligatoire: " + key);
            }
        }
    }

    // Serialise le snapshot de facture en JSON pour conservation du duplicata.
    private String toSnapshotJson(FactureResponse snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de serialiser le duplicata de facture", e);
        }
    }

    // Determine l'utilisateur courant a partir du contexte de securite.
    private String resolveActor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Evite les nulls en transformant les valeurs absentes en chaine vide.
    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    // Nettoie les chaines entrantes et convertit les blancs en null.
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // Valide qu'une chaine obligatoire est bien renseignee.
    private String requireNonBlank(String value, String message) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    // Force un format monetaire a deux decimales.
    private BigDecimal normalizeMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    // Calcule une taxe puis l'arrondit vers le haut pour respecter la contrainte fiscale de coherence.
    private BigDecimal roundTaxUp(BigDecimal baseTaxable, BigDecimal tauxTva) {
        BigDecimal rawTax = baseTaxable.multiply(tauxTva).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        return rawTax.setScale(2, RoundingMode.CEILING);
    }

    // Verifie les regles de cardinalite et d'unicite des commentaires facture.
    private void validateCommentaires(List<FactureCommentaireRequest> commentaires) {
        if (commentaires == null || commentaires.isEmpty()) {
            return;
        }
        if (commentaires.size() > 8) {
            throw new IllegalArgumentException("Une facture ne peut pas contenir plus de 8 lignes de commentaire");
        }

        List<CodeLigneCommentaire> codes = commentaires.stream().map(FactureCommentaireRequest::code).toList();
        long distinct = codes.stream().distinct().count();
        if (distinct != codes.size()) {
            throw new IllegalArgumentException("Les codes de commentaires doivent etre uniques");
        }
    }

    // Construit les lignes de commentaires depuis la requete ou le parametrage par defaut.
    private void attachCommentaires(Facture facture, List<FactureCommentaireRequest> commentairesRequest) {
        int order = 1;
        if (commentairesRequest == null || commentairesRequest.isEmpty()) {
            List<ParamLigneCommentaire> params = paramLigneCommentaireRepository.findByActifTrueOrderByCodeAsc();
            for (ParamLigneCommentaire p : params) {
                if (order > 8) {
                    break;
                }
                FactureCommentaire fc = new FactureCommentaire();
                fc.setFacture(facture);
                fc.setCode(p.getCode());
                fc.setEtiquette(requireNonBlank(p.getEtiquette(), "Etiquette commentaire invalide"));
                fc.setContenu(requireNonBlank(p.getContenuParDefaut(), "Contenu commentaire invalide"));
                fc.setOrdreAffichage(order++);
                facture.getCommentaires().add(fc);
            }
            return;
        }

        for (FactureCommentaireRequest req : commentairesRequest) {
            ParamLigneCommentaire param = paramLigneCommentaireRepository.findByCode(req.code()).orElse(null);

            String etiquette = trimToNull(req.etiquette());
            if (etiquette == null && param != null) {
                etiquette = trimToNull(param.getEtiquette());
            }
            if (etiquette == null) {
                etiquette = req.code().name();
            }

            String contenu = trimToNull(req.contenu());
            if (contenu == null && param != null) {
                contenu = trimToNull(param.getContenuParDefaut());
            }
            if (contenu == null) {
                throw new IllegalArgumentException("Le contenu du commentaire " + req.code() + " est obligatoire");
            }

            FactureCommentaire fc = new FactureCommentaire();
            fc.setFacture(facture);
            fc.setCode(req.code());
            fc.setEtiquette(etiquette);
            fc.setContenu(contenu);
            fc.setOrdreAffichage(order++);
            facture.getCommentaires().add(fc);
        }
    }

    // Calcule une empreinte SHA-256 pour assurer l'integrite des donnees transmises.
    private String generateHash(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de hash", ex);
        }
    }
}
