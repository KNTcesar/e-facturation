package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.GroupeTaxation;

import java.math.BigDecimal;

// Resume d'article pour le rapport A.
public record RapportArticleSummaryResponse(
        String codeArticle,
        String designation,
        String unite,
        GroupeTaxation groupeTaxation,
        BigDecimal prixUnitaire,
        BigDecimal tauxImpot,
        BigDecimal quantiteVendue,
        BigDecimal quantiteRetournee,
        BigDecimal quantiteEnStock,
        BigDecimal montantCollecte
) {
}
