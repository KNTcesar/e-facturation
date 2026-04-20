package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.GroupeTaxation;

import java.math.BigDecimal;
import java.util.UUID;

// Detail d'une ligne de facture avec montants calcules.
public record FactureLigneResponse(
        UUID id,
        String description,
        BigDecimal quantite,
        BigDecimal prixUnitaireHt,
        BigDecimal tauxTva,
        GroupeTaxation groupeTaxation,
        BigDecimal montantTaxeSpecifique,
        BigDecimal baseTaxableTva,
        BigDecimal montantHt,
        BigDecimal montantTva,
        BigDecimal montantTtc,
        FactureProduitInfoResponse produit
) {
}
