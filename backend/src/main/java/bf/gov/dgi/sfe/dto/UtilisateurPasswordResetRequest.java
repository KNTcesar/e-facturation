package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Payload de réinitialisation de mot de passe.
public record UtilisateurPasswordResetRequest(
        @NotBlank String newPassword
) {
}
