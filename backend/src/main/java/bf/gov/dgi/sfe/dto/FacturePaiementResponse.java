package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;
import java.util.UUID;

import bf.gov.dgi.sfe.enums.ModePaiement;

// Reponse d'un paiement associe a une facture.
public record FacturePaiementResponse(
        UUID id,
        ModePaiement modePaiement,
        BigDecimal montant,
        String referencePaiement
) {
}
