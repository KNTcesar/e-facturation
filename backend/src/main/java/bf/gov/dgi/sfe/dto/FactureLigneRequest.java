package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// Ligne de facture envoyee depuis le frontend.
public record FactureLigneRequest(
        @NotNull UUID produitId,
        @NotNull @DecimalMin("0.001") @Digits(integer = 15, fraction = 3) BigDecimal quantite
) {
}
