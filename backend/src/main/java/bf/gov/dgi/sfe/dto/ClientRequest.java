package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import bf.gov.dgi.sfe.enums.TypeClient;

// Donnees saisies pour creer ou modifier un client.
public record ClientRequest(
        @NotNull TypeClient typeClient,
        @NotBlank String nom,
        String ifu,
        String rccm,
        @NotBlank String adresse,
        String telephone,
        String email
) {
}
