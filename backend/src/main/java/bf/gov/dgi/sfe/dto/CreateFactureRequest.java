package bf.gov.dgi.sfe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Requete complete de creation de facture.
public record CreateFactureRequest(
        @NotNull UUID clientId,
        @NotBlank String serieCode,
        @NotNull LocalDate dateEmission,
        @NotBlank String referenceMarche,
        @NotBlank String objetMarche,
        @NotNull LocalDate dateMarche,
        @NotNull LocalDate dateDebutExecution,
        @NotNull LocalDate dateFinExecution,
        @NotEmpty List<@Valid FactureLigneRequest> lignes
) {
}
