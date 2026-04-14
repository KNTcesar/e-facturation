package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Detail d'une ligne de facture avec montants calcules.
public record FactureLigneResponse(
        UUID id,
        String description,
        BigDecimal quantite,
        BigDecimal prixUnitaireHt,
        BigDecimal tauxTva,
        BigDecimal montantHt,
        BigDecimal montantTva,
        BigDecimal montantTtc,
        FactureProduitInfoResponse produit
) {
}
