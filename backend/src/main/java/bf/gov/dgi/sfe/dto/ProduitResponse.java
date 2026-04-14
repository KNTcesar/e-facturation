package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Donnees retour pour un produit ou service.
public record ProduitResponse(
        UUID id,
        String reference,
        String designation,
        BigDecimal prixUnitaireHt,
        BigDecimal tauxTva,
        String unite
) {
}
