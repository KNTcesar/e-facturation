package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeRapportReglementaire;
import bf.gov.dgi.sfe.enums.NatureRapportReglementaire;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

// Reponse complete d'un rapport reglementaire X, Z ou A.
public record RapportReglementaireResponse(
        UUID id,
        TypeRapportReglementaire typeRapport,
        NatureRapportReglementaire natureRapport,
        String nomCommercial,
        String ifu,
        String isf,
        LocalDate dateDebut,
        LocalDate dateFin,
        OffsetDateTime generatedAt,
        String generatedBy,
        long nombreFactures,
        BigDecimal totalHt,
        BigDecimal totalTva,
        BigDecimal totalTtc,
        BigDecimal totalReductionsCommerciales,
        long nombreAutresEnregistrementsReductionVentes,
        BigDecimal montantAutresEnregistrementsReductionVentes,
        long ventesIncompletes,
        List<RapportTypeFactureSummaryResponse> facturesParType,
        List<RapportTaxationTypeFactureSummaryResponse> montantsParGroupeTaxationEtTypeFacture,
        List<RapportModePaiementSummaryResponse> paiementsParMode,
        List<RapportArticleSummaryResponse> articles
) {
}
