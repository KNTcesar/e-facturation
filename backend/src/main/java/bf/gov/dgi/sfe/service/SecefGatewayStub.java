package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.TransmissionSecef;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

// Simule l'appel reseau SECeF tant que le connecteur officiel n'est pas integre.
@Component
public class SecefGatewayStub {

    // Simule l'envoi au SECeF avec controles d'integrite et resultat deterministe.
    public SecefGatewayResult submit(TransmissionSecef tx) {
        String payloadData = tx.getPayloadData();
        if (payloadData == null || payloadData.isBlank()) {
            return new SecefGatewayResult(false, "EMPTY_SECURITY_PAYLOAD", "Payload de securite manquant pour la transmission MCF");
        }

        String printedPayload = tx.getFacture().getQrPayload();
        if (!payloadData.equals(printedPayload)) {
            return new SecefGatewayResult(false, "SECURITY_DATA_MISMATCH", "Les donnees transmises au MCF sont differentes des donnees imprimees sur la facture");
        }

        if (!sha256Hex(payloadData).equals(tx.getPayloadHash())) {
            return new SecefGatewayResult(false, "PAYLOAD_HASH_MISMATCH", "Empreinte de securite invalide");
        }

        // Strategie deterministe: certains payloads echouent temporairement pour tester le retry.
        int bucket = Math.floorMod(tx.getPayloadHash().hashCode() + tx.getRetryCount(), 5);
        if (bucket == 0 && tx.getRetryCount() < 2) {
            return new SecefGatewayResult(false, "TEMP_UNAVAILABLE", "SECeF temporairement indisponible");
        }
        return new SecefGatewayResult(true, "QUEUED", "Transmission acceptee par la passerelle SECeF");
    }

    // Calcule le hash SHA-256 d'une charge utile de transmission.
    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de hash payload", ex);
        }
    }

    // Resultat simplifie du connecteur SECeF simule.
    public record SecefGatewayResult(boolean success, String codeRetour, String messageRetour) {
    }
}