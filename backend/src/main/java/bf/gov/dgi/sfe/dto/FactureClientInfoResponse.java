package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Informations client destinataire dans la vue facture detaillee.
public record FactureClientInfoResponse(
        UUID id,
        String nom,
        String ifu,
        String rccm,
        String adresse,
        String telephone,
        String email
) {
}
