package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Requete de configuration d'un certificat fiscal.
public record CertificatFiscalRequest(
        @NotBlank String numeroSerie,
        @NotBlank String numeroIsf,
        @NotBlank String autoriteEmission,
        @NotNull LocalDate dateDebutValidite,
        @NotNull LocalDate dateFinValidite,
        boolean actif
) {
}
