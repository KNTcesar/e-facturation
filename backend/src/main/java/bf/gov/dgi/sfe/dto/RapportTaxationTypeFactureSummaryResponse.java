package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.TypeFacture;

import java.math.BigDecimal;

// Resume des montants par groupe de taxation pour chaque type de facture.
public record RapportTaxationTypeFactureSummaryResponse(
        TypeFacture typeFacture,
        GroupeTaxation groupeTaxation,
        BigDecimal montantTotal,
        BigDecimal montantTaxable,
        BigDecimal montantTaxe
) {
}