package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeFacture;

import java.math.BigDecimal;

// Resume des montants par type de facture.
public record RapportTypeFactureSummaryResponse(
        TypeFacture typeFacture,
        long nombreFactures,
        BigDecimal totalHt,
        BigDecimal totalTva,
        BigDecimal totalTtc
) {
}
