package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;
import jakarta.validation.constraints.NotNull;

// Ligne de commentaire optionnelle lors de l'emission de facture.
public record FactureCommentaireRequest(
        @NotNull CodeLigneCommentaire code,
        String etiquette,
        String contenu
) {
}
