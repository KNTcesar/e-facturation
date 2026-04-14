package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// Donnees de creation d'un produit ou service.
public record ProduitRequest(
        @NotBlank String reference,
        @NotBlank String designation,
        @NotNull @DecimalMin("0.00") BigDecimal prixUnitaireHt,
        @NotNull @DecimalMin("0.00") BigDecimal tauxTva,
        @NotBlank String unite
) {
}
