package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.TransmissionSecef;
import org.springframework.stereotype.Component;

// Simule l'appel reseau SECeF tant que le connecteur officiel n'est pas integre.
@Component
public class SecefGatewayStub {

    public SecefGatewayResult submit(TransmissionSecef tx) {
        // Strategie deterministe: certains payloads echouent temporairement pour tester le retry.
        int bucket = Math.floorMod(tx.getPayloadHash().hashCode() + tx.getRetryCount(), 5);
        if (bucket == 0 && tx.getRetryCount() < 2) {
            return new SecefGatewayResult(false, "TEMP_UNAVAILABLE", "SECeF temporairement indisponible");
        }
        return new SecefGatewayResult(true, "QUEUED", "Transmission acceptee par la passerelle SECeF");
    }

    public record SecefGatewayResult(boolean success, String codeRetour, String messageRetour) {
    }
}