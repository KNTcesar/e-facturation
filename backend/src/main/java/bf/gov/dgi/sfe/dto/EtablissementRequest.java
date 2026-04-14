package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Requete de configuration d'un etablissement fiscal.
public record EtablissementRequest(
        @NotBlank String code,
        @NotBlank String nom,
        @NotBlank String adresse,
        @NotBlank String ville,
        boolean principal,
        boolean actif
) {
}
