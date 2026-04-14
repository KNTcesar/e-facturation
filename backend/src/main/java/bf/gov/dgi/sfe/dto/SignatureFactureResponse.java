package bf.gov.dgi.sfe.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

// Retour de signature de facture
public record SignatureFactureResponse(
        UUID id,
        UUID factureId,
        String dataBrute,
        String algorithme,
        String certificatFingerprint,
        OffsetDateTime dateSignature,
        boolean verifie
) {
}
