package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Informations entreprise emettrice visibles sur la facture client.
public record FactureEntrepriseInfoResponse(
        UUID id,
        String nom,
        String ifu,
        String rccm,
        String adresse,
        String ville,
        String paysCode,
        String telephone,
        String email,
        String logoUrl
) {
}
