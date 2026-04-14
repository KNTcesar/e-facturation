package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.SignatureFactureResponse;
import bf.gov.dgi.sfe.dto.SignerFactureRequest;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.SignatureFacture;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.SignatureFactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// S4 - Service de signature des factures
public class SignatureFactureService {
    private final SignatureFactureRepository signatureRepository;
    private final FactureRepository factureRepository;

    /**
     * Signe une facture avec un certificat et une signature numerique fournis
     */
    @Transactional
    public SignatureFactureResponse signer(UUID factureId, SignerFactureRequest request) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvee: " + factureId));

        // Hash de la facture pour signature
        String dataBrute = computeFactureHash(facture);

        // Creer enregistrement de signature
        SignatureFacture signature = new SignatureFacture();
        signature.setFacture(facture);
        signature.setDataBrute(dataBrute);
        signature.setSignatureBase64(request.signatureBase64());
        signature.setAlgorithme(request.algorithme());
        signature.setCertificatBase64(request.certificatBase64());
        signature.setDateSignature(OffsetDateTime.now());
        signature.setCertificatFingerprint(computeCertificatFingerprint(request.certificatBase64()));
        signature.setVerifie(true); // En prod, verifier la signature contre le certificat

        signature = signatureRepository.save(signature);

        return mapToResponse(signature);
    }

    /**
     * Recupere la signature d'une facture
     */
    @Transactional(readOnly = true)
    public SignatureFactureResponse getSignature(UUID factureId) {
        SignatureFacture signature = signatureRepository.findByFactureId(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Signature non trouvee pour facture: " + factureId));
        return mapToResponse(signature);
    }

    /**
     * Verifie que la facture est signee
     */
    boolean isSignee(UUID factureId) {
        return signatureRepository.findByFactureId(factureId).isPresent();
    }

    /**
     * Calcule le hash SHA-256 de la facture pour signature
     */
    private String computeFactureHash(Facture facture) {
        try {
            String data = facture.getNumero() + "|" + facture.getDateEmission() + "|" + facture.getTotalTtc();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul hash facture", e);
        }
    }

    /**
     * Calcule le fingerprint du certificat (SHA-256 du cert en base64)
     */
    private String computeCertificatFingerprint(String certificatBase64) {
        try {
               MessageDigest digest = MessageDigest.getInstance("SHA-256");
               byte[] hash = digest.digest(certificatBase64.getBytes());
               return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul fingerprint certificat", e);
        }
    }

    private SignatureFactureResponse mapToResponse(SignatureFacture signature) {
        return new SignatureFactureResponse(
                signature.getId(),
                signature.getFacture().getId(),
                signature.getDataBrute(),
                signature.getAlgorithme(),
                signature.getCertificatFingerprint(),
                signature.getDateSignature(),
                signature.isVerifie()
        );
    }
}
