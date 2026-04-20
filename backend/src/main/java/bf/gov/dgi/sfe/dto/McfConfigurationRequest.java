package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeConnexionMcf;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Requete de mise a jour du port MCF.
public record McfConfigurationRequest(
        TypeConnexionMcf typeConnexion,
        @NotBlank String host,
        @Min(1) @Max(65535) int port
) {
}