package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Donnees pour parametrer une serie de numerotation.
public record SerieFactureRequest(
        @NotBlank String code,
        int exercice,
        long prochainNumero,
        boolean active
) {
}
