package bf.gov.dgi.sfe.dto;

import java.util.UUID;

// Retour d'un compte bancaire configure pour l'entreprise.
public record CompteBancaireEntrepriseResponse(
        UUID id,
        String referenceCompte,
        String banque,
        boolean actif
) {
}