package bf.gov.dgi.sfe.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

// Retour d'horodatage de facture
public record HorodatageFactureResponse(
        UUID id,
        UUID factureId,
        String algorithmeHash,
        String authoriteTemps,
        OffsetDateTime dateHorodatage,
        boolean verifie
) {
}
