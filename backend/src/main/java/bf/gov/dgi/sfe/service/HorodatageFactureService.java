package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.HorodatageFactureResponse;
import bf.gov.dgi.sfe.dto.HorodaterFactureRequest;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.HorodatageFacture;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.HorodatageFactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// S4 - Service d'horodatage (timestamping) des factures
public class HorodatageFactureService {
    private final HorodatageFactureRepository horodatageRepository;
    private final FactureRepository factureRepository;

    /**
     * Applique un horodatage a une facture a partir d'une autorite de temps
     */
    @Transactional
    public HorodatageFactureResponse horodater(UUID factureId, HorodaterFactureRequest request) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture non trouvee: " + factureId));

        // Hash du document/signature a horodater
        String hashDocumented = computeHashSignature(facture);

        // Creer enregistrement d'horodatage
        HorodatageFacture horodatage = new HorodatageFacture();
        horodatage.setFacture(facture);
        horodatage.setHashDocumented(hashDocumented);
        horodatage.setTokenHorodatageBase64(request.tokenHorodatageBase64());
        horodatage.setAuthoriteTemps(request.authoriteTemps());
        horodatage.setAlgorithmeHash(request.algorithmeHash());
        horodatage.setDateHorodatage(OffsetDateTime.now());
        horodatage.setUrlVerification("https://tsa.example.bf/verify");
        horodatage.setVerifie(true); // En prod, verifier le token TSA

        horodatage = horodatageRepository.save(horodatage);

        return mapToResponse(horodatage);
    }

    /**
     * Recupere l'horodatage d'une facture
     */
    @Transactional(readOnly = true)
    public HorodatageFactureResponse getHorodatage(UUID factureId) {
        HorodatageFacture horodatage = horodatageRepository.findByFactureId(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Horodatage non trouve pour facture: " + factureId));
        return mapToResponse(horodatage);
    }

    /**
     * Verifie que la facture est horodatee
     */
    boolean isHorodatee(UUID factureId) {
        return horodatageRepository.findByFactureId(factureId).isPresent();
    }

    /**
     * Calcule le hash SHA-256 de la signature/document a horodater
     */
    private String computeHashSignature(Facture facture) {
        try {
            // En prod, ce serait le hash de la signature numerique
            String data = facture.getCodeAuthentification() + "|" + facture.getDateCertification();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul hash signature", e);
        }
    }

    private HorodatageFactureResponse mapToResponse(HorodatageFacture horodatage) {
        return new HorodatageFactureResponse(
                horodatage.getId(),
                horodatage.getFacture().getId(),
                horodatage.getAlgorithmeHash(),
                horodatage.getAuthoriteTemps(),
                horodatage.getDateHorodatage(),
                horodatage.isVerifie()
        );
    }
}
