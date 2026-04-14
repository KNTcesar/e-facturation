package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Retour d'un etablissement fiscal.
public record EtablissementResponse(
        UUID id,
        String code,
        String nom,
        String adresse,
        String ville,
        boolean principal,
        boolean actif
) {
}
