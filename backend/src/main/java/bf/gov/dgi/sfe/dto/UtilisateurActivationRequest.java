package bf.gov.dgi.sfe.dto;

// Payload simple pour activer ou désactiver un compte.
public record UtilisateurActivationRequest(
        boolean actif
) {
}
