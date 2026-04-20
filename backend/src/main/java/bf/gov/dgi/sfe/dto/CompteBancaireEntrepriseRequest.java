package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Donnees d'un compte bancaire configure pour l'entreprise.
public record CompteBancaireEntrepriseRequest(
        @NotBlank String referenceCompte,
        String banque,
        boolean actif
) {
}