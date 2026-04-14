package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.StatutTransmission;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransmissionSecefResponse(
        UUID id,
        UUID factureId,
        String formatPayload,
        String payloadHash,
        StatutTransmission statut,
        String codeRetour,
        String messageRetour,
        OffsetDateTime dateEnvoi,
        OffsetDateTime dateAccuse,
        int retryCount
) {
}
