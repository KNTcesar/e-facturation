package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeDocumentJournalElectronique;

import java.time.OffsetDateTime;
import java.util.UUID;

// Ligne retournee du journal electronique.
public record JournalElectroniqueResponse(
        UUID id,
        TypeDocumentJournalElectronique typeDocument,
        UUID documentId,
        String referenceDocument,
        String codeSecefDgi,
        String contenuJson,
        OffsetDateTime createdAt
) {
}