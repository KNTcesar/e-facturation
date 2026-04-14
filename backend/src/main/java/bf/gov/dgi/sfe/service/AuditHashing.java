package bf.gov.dgi.sfe.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

// Utilitaire centralise de hash pour le chainage du journal d'audit.
final class AuditHashing {

    private AuditHashing() {
    }

    static String computeEntryHash(long sequenceNumber,
                                   String previousEntryHash,
                                   String action,
                                   String entite,
                                   UUID entiteId,
                                   String acteur,
                                   String oldHash,
                                   String newHash,
                                   String details) {
        String payload = sequenceNumber
                + "|" + normalize(previousEntryHash)
                + "|" + normalize(action)
                + "|" + normalize(entite)
                + "|" + normalizeUuid(entiteId)
                + "|" + normalize(acteur)
                + "|" + normalize(oldHash)
                + "|" + normalize(newHash)
                + "|" + normalize(details);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de calcul hash audit", ex);
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private static String normalizeUuid(UUID value) {
        return value == null ? "-" : value.toString();
    }
}
