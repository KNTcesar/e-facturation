package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Payload de connexion utilisateur.
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
