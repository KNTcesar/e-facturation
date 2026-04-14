package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LigneFactureResponse(
        UUID id,
        UUID factureId,
        UUID produitId,
        String description,
        BigDecimal quantite,
        BigDecimal prixUnitaireHt,
        BigDecimal tauxTva,
        BigDecimal montantHt,
        BigDecimal montantTva,
        BigDecimal montantTtc
) {
}
