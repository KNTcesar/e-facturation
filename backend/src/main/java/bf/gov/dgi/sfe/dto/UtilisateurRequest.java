package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

// Donnees de creation d'un compte utilisateur.
public record UtilisateurRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String nomComplet,
        @NotEmpty Set<String> roles
) {
}
