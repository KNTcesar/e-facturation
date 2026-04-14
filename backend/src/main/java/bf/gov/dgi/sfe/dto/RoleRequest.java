package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Donnees pour creer un role applicatif.
public record RoleRequest(
        @NotBlank String code,
        @NotBlank String libelle
) {
}
