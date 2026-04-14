package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Donnees saisies pour creer ou modifier un client.
public record ClientRequest(
        @NotBlank String nom,
        @NotBlank String ifu,
        @NotBlank String adresse,
        String telephone,
        String email
) {
}
