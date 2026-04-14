package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.StatutTransmission;

import java.util.UUID;

// Resume d'un envoi technique vers le SECeF.
public record SecefDispatchResponse(
        UUID transmissionId,
        UUID factureId,
        StatutTransmission statut,
        String codeRetour,
        String messageRetour,
        int retryCount
) {
}