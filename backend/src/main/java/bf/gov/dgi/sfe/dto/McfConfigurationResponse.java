package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeConnexionMcf;

import java.time.OffsetDateTime;
import java.util.UUID;

// Retour de la configuration active du port MCF.
public record McfConfigurationResponse(
        UUID id,
        TypeConnexionMcf typeConnexion,
        String host,
        int port,
        boolean actif,
        OffsetDateTime updatedAt
) {
}