package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeMouvementNumeraire;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

// Reponse de mouvement de numeraires.
public record MouvementNumeraireResponse(
        UUID id,
        TypeMouvementNumeraire typeMouvement,
        BigDecimal montant,
        LocalDate dateOperation,
        String motif,
        String acteur,
        OffsetDateTime createdAt
) {
}