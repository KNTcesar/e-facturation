package bf.gov.dgi.sfe.dto;

import java.time.LocalDate;
import java.util.UUID;

// Retour d'un certificat fiscal.
public record CertificatFiscalResponse(
        UUID id,
        String numeroSerie,
        String numeroIsf,
        String autoriteEmission,
        LocalDate dateDebutValidite,
        LocalDate dateFinValidite,
        boolean actif
) {
}
