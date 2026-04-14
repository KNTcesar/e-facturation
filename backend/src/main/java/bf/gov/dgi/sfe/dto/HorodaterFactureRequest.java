package bf.gov.dgi.sfe.dto;

// Request pour appliquer un horodatage
public record HorodaterFactureRequest(
        String tokenHorodatageBase64,
        String authoriteTemps,
        String algorithmeHash
) {
}
