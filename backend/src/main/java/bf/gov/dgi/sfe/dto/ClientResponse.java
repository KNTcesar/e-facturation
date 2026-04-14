package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Representation retournee au frontend pour un client.
public record ClientResponse(
        UUID id,
        String nom,
        String ifu,
        String adresse,
        String telephone,
        String email
) {
}
