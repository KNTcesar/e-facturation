package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.TypeFacture;
import bf.gov.dgi.sfe.enums.NatureFactureAvoir;
import bf.gov.dgi.sfe.enums.GroupePsvb;
import bf.gov.dgi.sfe.enums.ModePrixArticle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Reponse resumee de facture au frontend.
public record FactureResponse(
        UUID id,
        String numero,
        LocalDate dateEmission,
        TypeFacture typeFacture,
        ModePrixArticle modePrixUnitaire,
        NatureFactureAvoir natureFactureAvoir,
        String referenceFactureOriginale,
        StatutFacture statut,
        int exercice,
        BigDecimal totalHt,
        BigDecimal totalTva,
        BigDecimal totalTtc,
        GroupePsvb groupePsvb,
        BigDecimal tauxPsvb,
        BigDecimal montantPsvb,
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
        List<FactureCommentaireResponse> commentaires,
        List<FacturePaiementResponse> paiements,
        List<FactureLigneResponse> lignes
) {
}
