package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;
import java.util.UUID;

import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import bf.gov.dgi.sfe.enums.TypeArticle;

// Donnees retour pour un produit ou service.
public record ProduitResponse(
        UUID id,
        String reference,
        String designation,
        TypeArticle typeArticle,
        ModePrixArticle modePrixArticle,
        BigDecimal prixUnitaireHt,
        BigDecimal prixUnitaireTtc,
        BigDecimal tauxTva,
        GroupeTaxation groupeTaxation,
        BigDecimal taxeSpecifiqueUnitaire,
        String unite,
        BigDecimal quantite
) {
}
