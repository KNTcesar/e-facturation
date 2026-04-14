package bf.gov.dgi.sfe.dto;

import java.util.Set;
import java.util.UUID;

// Retour utilisateur sans mot de passe.
public record UtilisateurResponse(
        UUID id,
        String username,
        String nomComplet,
        boolean actif,
        Set<String> roles
) {
}
