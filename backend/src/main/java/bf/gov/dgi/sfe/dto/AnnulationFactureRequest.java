package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Requete d'annulation d'une facture deja emise.
public record AnnulationFactureRequest(
        @NotBlank String motif
) {
}
