package bf.gov.dgi.sfe.dto;

import java.util.Set;

// Reponse de connexion avec jeton JWT et roles.
public record AuthResponse(
        String token,
        String username,
        Set<String> roles
) {
}
