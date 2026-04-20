package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import bf.gov.dgi.sfe.enums.TypeArticle;

import java.math.BigDecimal;

// Donnees de creation d'un produit ou service.
public record ProduitRequest(
        @NotBlank String reference,
        @NotBlank @Size(max = 255) String designation,
        @NotNull TypeArticle typeArticle,
        @NotNull ModePrixArticle modePrixArticle,
        @NotNull @DecimalMin("0.00") @Digits(integer = 15, fraction = 2) BigDecimal prixUnitaire,
        @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal tauxTva,
        GroupeTaxation groupeTaxation,
        @DecimalMin("0.00") @Digits(integer = 15, fraction = 2) BigDecimal taxeSpecifiqueUnitaire,
        @NotBlank String unite,
        @NotNull @DecimalMin("0.000") @Digits(integer = 15, fraction = 3) BigDecimal quantite
) {
}
