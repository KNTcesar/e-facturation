package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Retour de controle d'integrite du chainage du journal d'audit.
public record AuditChainVerificationResponse(
        boolean valid,
        int totalEntries,
        UUID firstBrokenEntryId,
        String message
) {
}
