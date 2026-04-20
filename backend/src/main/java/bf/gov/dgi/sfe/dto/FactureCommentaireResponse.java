package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;

import java.util.UUID;

// Ligne de commentaire exposee dans le detail d'une facture.
public record FactureCommentaireResponse(
        UUID id,
        CodeLigneCommentaire code,
        String etiquette,
        String contenu,
        int ordreAffichage
) {
}
