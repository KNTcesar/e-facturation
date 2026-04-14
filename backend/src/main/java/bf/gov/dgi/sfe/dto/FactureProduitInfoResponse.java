package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Informations produit exposees dans chaque ligne de facture.
public record FactureProduitInfoResponse(
        UUID id,
        String reference,
        String designation,
        String unite
) {
}
