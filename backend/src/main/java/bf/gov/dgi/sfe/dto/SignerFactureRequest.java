package bf.gov.dgi.sfe.dto;

import java.time.OffsetDateTime;

// Request pour signer une facture
public record SignerFactureRequest(
        String certificatBase64,
        String signatureBase64,
        String algorithme
) {
}
