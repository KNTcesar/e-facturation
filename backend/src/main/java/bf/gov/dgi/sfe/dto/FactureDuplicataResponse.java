package bf.gov.dgi.sfe.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

// Reponse d'historique d'un duplicata de facture.
public record FactureDuplicataResponse(
        UUID id,
        UUID factureId,
        String numeroFacture,
        LocalDate dateFacture,
        String demandeur,
        String motif,
        String empreinteSnapshot,
        OffsetDateTime dateGeneration
) {
}