package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeMouvementStock;
import bf.gov.dgi.sfe.enums.TypeOrigineMouvementStock;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

// Reponse API d'un mouvement de stock journalise.
public record MouvementStockResponse(
        UUID id,
        UUID produitId,
        String produitReference,
        String produitDesignation,
        TypeMouvementStock typeMouvement,
        TypeOrigineMouvementStock origineType,
        BigDecimal quantite,
        BigDecimal stockAvant,
        BigDecimal stockApres,
        String origineReference,
        String motif,
        String acteur,
        OffsetDateTime createdAt
) {
}
