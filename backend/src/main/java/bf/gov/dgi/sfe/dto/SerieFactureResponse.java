package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Reponse pour une serie de facture.
public record SerieFactureResponse(
        UUID id,
        String code,
        int exercice,
        long prochainNumero,
        boolean active
) {
}
