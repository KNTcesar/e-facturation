package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.ModePaiement;

import java.math.BigDecimal;

// Resume des paiements par mode.
public record RapportModePaiementSummaryResponse(
        ModePaiement modePaiement,
        BigDecimal totalMontant,
        long nombrePaiements
) {
}
