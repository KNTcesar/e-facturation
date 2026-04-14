package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Retour standard d'un role.
public record RoleResponse(
        UUID id,
        String code,
        String libelle
) {
}
