package bf.gov.dgi.sfe.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record JournalAuditResponse(
        UUID id,
        Long sequenceNumber,
        String previousEntryHash,
        String entryHash,
        String action,
        String entite,
        UUID entiteId,
        String oldHash,
        String newHash,
        String details,
        String acteur,
        OffsetDateTime createdAt
) {
}
