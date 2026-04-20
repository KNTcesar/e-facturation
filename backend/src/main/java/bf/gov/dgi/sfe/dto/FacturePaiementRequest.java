package bf.gov.dgi.sfe.dto;

import java.math.BigDecimal;

import bf.gov.dgi.sfe.enums.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// Ventilation d'un mode de paiement lors de la creation d'une facture.
public record FacturePaiementRequest(
        @NotNull ModePaiement modePaiement,
        @NotNull @DecimalMin("0.01") BigDecimal montant,
        String referencePaiement
) {
}
