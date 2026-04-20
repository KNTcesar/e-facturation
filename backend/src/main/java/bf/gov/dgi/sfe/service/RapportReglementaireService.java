package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.RapportArticleSummaryResponse;
import bf.gov.dgi.sfe.dto.RapportModePaiementSummaryResponse;
import bf.gov.dgi.sfe.dto.RapportReglementaireResponse;
import bf.gov.dgi.sfe.dto.RapportTaxationTypeFactureSummaryResponse;
import bf.gov.dgi.sfe.dto.RapportTypeFactureSummaryResponse;
import bf.gov.dgi.sfe.entity.Entreprise;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.FacturePaiement;
import bf.gov.dgi.sfe.entity.LigneFacture;
import bf.gov.dgi.sfe.entity.RapportReglementaireExecution;
import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.NatureRapportReglementaire;
import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.ModePaiement;
import bf.gov.dgi.sfe.enums.TypeFacture;
import bf.gov.dgi.sfe.enums.TypeRapportReglementaire;
import bf.gov.dgi.sfe.repository.EntrepriseRepository;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.RapportReglementaireExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Generation des rapports reglementaires X, Z et A.
public class RapportReglementaireService {

    private final FactureRepository factureRepository;
        private final EntrepriseRepository entrepriseRepository;
    private final RapportReglementaireExecutionRepository executionRepository;
    private final AuditService auditService;
        private final JournalElectroniqueService journalElectroniqueService;

    @Transactional(readOnly = true)
        // Retourne l'historique des generations de rapports reglementaires.
    public List<RapportReglementaireResponse> history() {
        return executionRepository.findAll().stream()
                                .sorted(Comparator.comparing(RapportReglementaireExecution::getCreatedAt).reversed())
                .map(this::buildFromExecution)
                .toList();
    }

    @Transactional
        // Genere un X-rapport sur une periode explicite ou implicite selon les parametres.
    public RapportReglementaireResponse generateX(LocalDate dateDebut, LocalDate dateFin) {
        return generate(TypeRapportReglementaire.X, dateDebut, dateFin);
    }

    @Transactional
        // Genere un Z-rapport pour la periode demandee.
    public RapportReglementaireResponse generateZ(LocalDate dateDebut, LocalDate dateFin) {
        return generate(TypeRapportReglementaire.Z, dateDebut, dateFin);
    }

    @Transactional
        // Genere un A-rapport avec le detail des articles sur la periode demandee.
    public RapportReglementaireResponse generateA(LocalDate dateDebut, LocalDate dateFin) {
        return generate(TypeRapportReglementaire.A, dateDebut, dateFin);
    }

        // Orchestration commune de generation: resolution de periode, calculs, audit et journal electronique.
    private RapportReglementaireResponse generate(TypeRapportReglementaire type, LocalDate dateDebut, LocalDate dateFin) {
        LocalDate resolvedFin = dateFin != null ? dateFin : LocalDate.now();
        LocalDate resolvedDebut = resolveStartDate(type, dateDebut, resolvedFin);
        NatureRapportReglementaire natureRapport = resolveNatureRapport(type, dateDebut, dateFin);

        List<Facture> factures = factureRepository.findAll().stream()
                .filter(f -> !f.getDateEmission().isBefore(resolvedDebut) && !f.getDateEmission().isAfter(resolvedFin))
                .filter(f -> f.getStatut() != null)
                .toList();

        RapportReglementaireResponse response = buildResponse(type, natureRapport, resolvedDebut, resolvedFin, factures);

        RapportReglementaireExecution execution = new RapportReglementaireExecution();
        execution.setTypeRapport(type);
        execution.setDateDebut(resolvedDebut);
        execution.setDateFin(resolvedFin);
        execution.setActeur(resolveActor());
        executionRepository.save(execution);

        auditService.trace("GENERATE_RAPPORT_" + type.name(), "RapportReglementaire", execution.getId(), resolveActor());
                journalElectroniqueService.recordRapport(response);
        return response;
    }

        // Determine la date de debut effective selon le type de rapport et l'historique disponible.
    private LocalDate resolveStartDate(TypeRapportReglementaire type, LocalDate requestedStart, LocalDate requestedEnd) {
        if (requestedStart != null) {
            return requestedStart;
        }

                if (type == TypeRapportReglementaire.X) {
                        return executionRepository.findTopByTypeRapportOrderByDateFinDesc(TypeRapportReglementaire.Z)
                                        .map(exec -> exec.getDateFin().plusDays(1))
                                        .orElseGet(() -> factureRepository.findAll().stream()
                                                        .map(Facture::getDateEmission)
                                                        .min(LocalDate::compareTo)
                                                        .orElse(requestedEnd));
                }

        return executionRepository.findTopByTypeRapportOrderByDateFinDesc(type)
                .map(exec -> exec.getDateFin().plusDays(1))
                .orElseGet(() -> factureRepository.findAll().stream()
                        .map(Facture::getDateEmission)
                        .min(LocalDate::compareTo)
                        .orElse(requestedEnd));
    }

        // Determine la nature fonctionnelle du rapport pour l'affichage (X quotidien/periodique, Z, A).
        private NatureRapportReglementaire resolveNatureRapport(TypeRapportReglementaire type, LocalDate dateDebut, LocalDate dateFin) {
                return switch (type) {
                        case Z -> NatureRapportReglementaire.Z_RAPPORT;
                        case A -> NatureRapportReglementaire.A_RAPPORT;
                        case X -> (dateDebut == null && dateFin == null)
                                        ? NatureRapportReglementaire.X_RAPPORT_QUOTIDIEN
                                        : NatureRapportReglementaire.X_RAPPORT_PERIODIQUE;
                };
        }

        // Construit les totaux et ventilations du rapport a partir des factures de la periode.
        private RapportReglementaireResponse buildResponse(TypeRapportReglementaire type,
                                                                                                           NatureRapportReglementaire natureRapport,
                                                                                                           LocalDate dateDebut,
                                                                                                           LocalDate dateFin,
                                                                                                           List<Facture> factures) {
                Entreprise entreprise = entrepriseRepository.findFirstByActifTrueOrderByDateEffetDesc().orElse(null);
                String nomCommercial = entreprise == null ? "" : entreprise.getNom();
                String ifu = entreprise == null ? "" : entreprise.getIfu();
                String isf = resolveIsf(entreprise);

        long nombreFactures = factures.size();
        BigDecimal totalHt = sumFactures(factures, Facture::getTotalHt);
        BigDecimal totalTva = sumFactures(factures, Facture::getTotalTva);
        BigDecimal totalTtc = sumFactures(factures, Facture::getTotalTtc);

        List<RapportTypeFactureSummaryResponse> parType = factures.stream()
                .collect(Collectors.groupingBy(Facture::getTypeFacture, () -> new EnumMap<>(TypeFacture.class), Collectors.toList()))
                .entrySet().stream()
                .map(entry -> new RapportTypeFactureSummaryResponse(
                        entry.getKey(),
                        entry.getValue().size(),
                        sumFactures(entry.getValue(), Facture::getTotalHt),
                        sumFactures(entry.getValue(), Facture::getTotalTva),
                        sumFactures(entry.getValue(), Facture::getTotalTtc)
                ))
                .sorted(Comparator.comparing(r -> r.typeFacture().name()))
                .toList();

        record TaxationKey(TypeFacture typeFacture, GroupeTaxation groupeTaxation) {}

        List<RapportTaxationTypeFactureSummaryResponse> parGroupeEtType = factures.stream()
                .flatMap(f -> f.getLignes().stream())
                .collect(Collectors.groupingBy(
                        ligne -> new TaxationKey(ligne.getFacture().getTypeFacture(), ligne.getGroupeTaxation()),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> new RapportTaxationTypeFactureSummaryResponse(
                        entry.getKey().typeFacture(),
                        entry.getKey().groupeTaxation(),
                        entry.getValue().stream().map(LigneFacture::getMontantTtc).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP),
                        entry.getValue().stream().map(LigneFacture::getBaseTaxableTva).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP),
                        entry.getValue().stream().map(LigneFacture::getMontantTva).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP)
                ))
                .sorted(Comparator
                        .comparing((RapportTaxationTypeFactureSummaryResponse r) -> r.typeFacture().name())
                        .thenComparing(r -> r.groupeTaxation().name()))
                .toList();

        List<RapportModePaiementSummaryResponse> parMode = factures.stream()
                .flatMap(f -> f.getPaiements().stream())
                .collect(Collectors.groupingBy(FacturePaiement::getModePaiement, () -> new EnumMap<>(ModePaiement.class), Collectors.toList()))
                .entrySet().stream()
                .map(entry -> new RapportModePaiementSummaryResponse(
                        entry.getKey(),
                        sumPayments(entry.getValue()),
                        entry.getValue().size()
                ))
                .sorted(Comparator.comparing(r -> r.modePaiement().name()))
                .toList();

        List<RapportArticleSummaryResponse> articles = type == TypeRapportReglementaire.A
                ? buildArticleSummaries(factures)
                : new ArrayList<>();

        BigDecimal totalReductionsCommerciales = factures.stream()
                .filter(f -> f.getTypeFacture() == TypeFacture.FA || f.getTypeFacture() == TypeFacture.EA)
                .map(Facture::getTotalTtc)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        long nombreAutresReductionsVentes = factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.ANNULEE)
                .count();

        BigDecimal montantAutresReductionsVentes = factures.stream()
                .filter(f -> f.getStatut() == StatutFacture.ANNULEE)
                .map(Facture::getTotalTtc)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        long ventesIncompletes = factures.stream().filter(f -> f.getStatut() == null || f.getStatut().name().equals("BROUILLON")).count();

        return new RapportReglementaireResponse(
                UUID.randomUUID(),
                type,
                natureRapport,
                nomCommercial,
                ifu,
                isf,
                dateDebut,
                dateFin,
                OffsetDateTime.now(),
                resolveActor(),
                nombreFactures,
                totalHt,
                totalTva,
                totalTtc,
                totalReductionsCommerciales,
                nombreAutresReductionsVentes,
                montantAutresReductionsVentes,
                ventesIncompletes,
                parType,
                parGroupeEtType,
                parMode,
                articles
        );
    }

        // Construit une vue historique simplifiee depuis une execution persistante.
    private RapportReglementaireResponse buildFromExecution(RapportReglementaireExecution execution) {
        return new RapportReglementaireResponse(
                execution.getId(),
                execution.getTypeRapport(),
                resolveNatureRapport(execution.getTypeRapport(), execution.getDateDebut(), execution.getDateFin()),
                "",
                "",
                "",
                execution.getDateDebut(),
                execution.getDateFin(),
                execution.getCreatedAt(),
                execution.getActeur(),
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                BigDecimal.ZERO,
                0,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

        // Agrege les informations articles (quantites et montants) pour le A-rapport.
    private List<RapportArticleSummaryResponse> buildArticleSummaries(List<Facture> factures) {
        Map<String, List<LigneFacture>> byArticle = factures.stream()
                .flatMap(f -> f.getLignes().stream())
                .collect(Collectors.groupingBy(l -> l.getProduit().getReference() + "|" + l.getProduit().getDesignation() + "|" + l.getProduit().getUnite()));

        return byArticle.entrySet().stream()
                .map(entry -> {
                    LigneFacture first = entry.getValue().get(0);
                    BigDecimal quantitySold = entry.getValue().stream()
                            .map(LigneFacture::getQuantite)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);
                    BigDecimal amountCollected = entry.getValue().stream()
                            .map(LigneFacture::getMontantTtc)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);
                    return new RapportArticleSummaryResponse(
                            first.getProduit().getReference(),
                            first.getProduit().getDesignation(),
                            first.getProduit().getUnite(),
                            first.getGroupeTaxation(),
                            first.getPrixUnitaireHt(),
                            first.getTauxTva(),
                            quantitySold,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            amountCollected
                    );
                })
                .sorted(Comparator.comparing(RapportArticleSummaryResponse::codeArticle))
                .toList();
    }

        // Somme un montant facture selon le mapper passe en parametre.
    private BigDecimal sumFactures(List<Facture> factures, java.util.function.Function<Facture, BigDecimal> mapper) {
        return factures.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

        // Somme les montants de paiements pour construire la ventilation par mode.
    private BigDecimal sumPayments(List<FacturePaiement> paiements) {
        return paiements.stream()
                .map(FacturePaiement::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

        // Identifie l'acteur courant responsable de la generation du rapport.
    private String resolveActor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return "system";
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

                // Recupere l'ISF actif et valide de l'entreprise a la date courante.
        private String resolveIsf(Entreprise entreprise) {
                if (entreprise == null || entreprise.getCertificats() == null) {
                        return "";
                }
                LocalDate today = LocalDate.now();
                return entreprise.getCertificats().stream()
                                .filter(cert -> cert.isActif()
                                                && (cert.getDateDebutValidite().isEqual(today) || cert.getDateDebutValidite().isBefore(today))
                                                && (cert.getDateFinValidite().isEqual(today) || cert.getDateFinValidite().isAfter(today)))
                                .map(cert -> cert.getNumeroIsf())
                                .findFirst()
                                .orElse("");
        }
}
