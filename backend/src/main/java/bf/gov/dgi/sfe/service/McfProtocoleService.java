package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.McfConfiguration;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
// Simule la commande protocolaire de verification de connectivite MCF.
public class McfProtocoleService {

    // Simule l'execution d'une commande protocolaire et retourne la derniere connexion estimee.
    public ConnectivityResult executerCommandeConnectivite(McfConfiguration configuration) {
        int bucket = Math.floorMod((configuration.getHost() + ":" + configuration.getPort()).hashCode(), 10);
        int jours = bucket <= 2 ? 8 + bucket : bucket % 4;
        OffsetDateTime lastConnection = OffsetDateTime.now().minusDays(jours);
        return new ConnectivityResult(lastConnection);
    }

    // Structure de retour contenant la date de derniere connexion a l'Administration.
    public record ConnectivityResult(OffsetDateTime dateDerniereConnexionAdministration) {
    }
}