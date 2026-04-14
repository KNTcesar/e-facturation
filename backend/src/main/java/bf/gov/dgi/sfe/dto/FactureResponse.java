package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Reponse resumee de facture au frontend.
public record FactureResponse(
        UUID id,
        String numero,
        LocalDate dateEmission,
        StatutFacture statut,
        int exercice,
        BigDecimal totalHt,
        BigDecimal totalTva,
        BigDecimal totalTtc,
        String codeAuthentification,
        String qrPayload,
        String motifAnnulation,
        String referenceMarche,
        String objetMarche,
        LocalDate dateMarche,
        LocalDate dateDebutExecution,
        LocalDate dateFinExecution,
        String validationDgi,
        FactureEntrepriseInfoResponse entreprise,
        FactureClientInfoResponse client,
        List<FactureLigneResponse> lignes
) {
}
