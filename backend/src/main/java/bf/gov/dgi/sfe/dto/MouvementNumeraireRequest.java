package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeMouvementNumeraire;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

// Requete d'enregistrement de mouvement de numeraires.
public record MouvementNumeraireRequest(
        @NotNull TypeMouvementNumeraire typeMouvement,
        @NotNull @DecimalMin("0.01") BigDecimal montant,
        @NotNull LocalDate dateOperation,
        String motif
) {
}