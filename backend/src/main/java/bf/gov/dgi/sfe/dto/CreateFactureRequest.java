package bf.gov.dgi.sfe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import bf.gov.dgi.sfe.enums.NatureFactureAvoir;
import bf.gov.dgi.sfe.enums.TypeFacture;
import bf.gov.dgi.sfe.enums.GroupePsvb;
import bf.gov.dgi.sfe.enums.ModePrixArticle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Requete complete de creation de facture.
public record CreateFactureRequest(
        @NotNull UUID clientId,
        @NotBlank String serieCode,
        @NotNull LocalDate dateEmission,
        TypeFacture typeFacture,
        ModePrixArticle modePrixUnitaire,
        NatureFactureAvoir natureFactureAvoir,
        String referenceFactureOriginale,
        GroupePsvb groupePsvb,
        String referenceMarche,
        String objetMarche,
        LocalDate dateMarche,
        LocalDate dateDebutExecution,
        LocalDate dateFinExecution,
        List<@Valid FactureCommentaireRequest> commentaires,
        List<@Valid FacturePaiementRequest> paiements,
        @NotEmpty List<@Valid FactureLigneRequest> lignes
) {
}
