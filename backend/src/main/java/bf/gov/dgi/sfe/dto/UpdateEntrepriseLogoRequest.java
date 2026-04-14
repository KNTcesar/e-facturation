package bf.gov.dgi.sfe.dto;

// Request pour mettre à jour simplement le logo d'une entreprise.
public record UpdateEntrepriseLogoRequest(
        String logoUrl
) {
}
