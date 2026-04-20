package bf.gov.dgi.sfe.dto;

import bf.gov.dgi.sfe.enums.TypeConnexionMcf;

import java.time.OffsetDateTime;
import java.util.UUID;

// Retour du statut de connectivite MCF pour affichage UI.
public record McfConnectiviteStatutResponse(
        UUID id,
        UUID configurationId,
        TypeConnexionMcf typeConnexion,
        String host,
        int port,
        boolean connecteAdministration,
        OffsetDateTime dateDerniereConnexionAdministration,
        OffsetDateTime dateDerniereVerification,
        int joursDepuisDerniereConnexion,
        boolean alerteActive,
        String messageAlerte
) {
}