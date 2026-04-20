package bf.gov.dgi.sfe.dto;

import java.util.UUID;

import bf.gov.dgi.sfe.enums.TypeClient;

// Representation retournee au frontend pour un client.
public record ClientResponse(
        UUID id,
        TypeClient typeClient,
        String nom,
        String ifu,
        String rccm,
        String adresse,
        String telephone,
        String email
) {
}
