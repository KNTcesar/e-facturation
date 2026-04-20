package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.McfConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// Simule l'obtention des elements de securite aupres du MCF.
public class McfSecurityService {

    private final McfConfigurationService mcfConfigurationService;

    // Simule la recuperation des elements de securite MCF pour une facture.
    public McfSecurityElements obtainSecurityElements(String numeroFacture, BigDecimal totalTtc, UUID clientId) {
        McfConfiguration cfg = mcfConfigurationService.resolveActiveConfiguration();
        String fingerprint = sha256Hex("MCF|" + cfg.getHost() + "|" + cfg.getPort() + "|" + numeroFacture + "|" + totalTtc + "|" + clientId);
        String codeAuthentification = fingerprint.substring(0, 16).toUpperCase();
        return new McfSecurityElements(codeAuthentification, "MCF-STUB:" + cfg.getHost() + ":" + cfg.getPort());
    }

    // Calcule une empreinte SHA-256 hexadecimale.
    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Erreur de hash MCF", ex);
        }
    }

    // Objet de retour contenant le code d'authentification et la source de securite.
    public record McfSecurityElements(String codeAuthentification, String sourceSecurite) {
    }
}
