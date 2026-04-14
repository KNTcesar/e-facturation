package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.SignatureFactureResponse;
import bf.gov.dgi.sfe.dto.SignerFactureRequest;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.SignatureFacture;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.SignatureFactureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// S4 - Tests de signature des factures
public class SignatureFactureServiceTest {
    @Mock
    private SignatureFactureRepository signatureRepository;

    @Mock
    private FactureRepository factureRepository;

    @InjectMocks
    private SignatureFactureService signatureService;

    private UUID factureId;
    private Facture mockFacture;

    @BeforeEach
    void setUp() {
        factureId = UUID.randomUUID();
        mockFacture = createMockFacture();
    }

    @Test
    void testSigner_Success() {
        // Arrange
        String certificatBase64 = "MIIDXTCCAkWgAwIBAgIJAJXN2YZZ4MZOMA0GCSqGSIb3DQEBBQUAMEUxCzAJBgNV=";
        String signatureBase64 = "MIIEkQIBAAKCAQEA2qWez...";
        String algorithme = "RSA-SHA256";

        mockFacture.setId(factureId); // Ensure facture has correct ID
        when(factureRepository.findById(factureId)).thenReturn(Optional.of(mockFacture));
        when(signatureRepository.save(any(SignatureFacture.class)))
                .thenAnswer(inv -> {
                    SignatureFacture sig = inv.getArgument(0);
                    sig.setId(UUID.randomUUID());
                    return sig;
                });

        SignerFactureRequest request = new SignerFactureRequest(certificatBase64, signatureBase64, algorithme);

        // Act
        SignatureFactureResponse response = signatureService.signer(factureId, request);

        // Assert
        assertNotNull(response);
        assertEquals(factureId, response.factureId());
        assertEquals(algorithme, response.algorithme());
        assertTrue(response.verifie());
        verify(factureRepository, times(1)).findById(factureId);
        verify(signatureRepository, times(1)).save(any(SignatureFacture.class));
    }

    @Test
    void testGetSignature_Success() {
        // Arrange
        mockFacture.setId(factureId); // Ensure facture has correct ID
        SignatureFacture signature = new SignatureFacture();
        signature.setId(UUID.randomUUID());
        signature.setFacture(mockFacture);
        signature.setAlgorithme("RSA-SHA256");
        signature.setVerifie(true);

        when(signatureRepository.findByFactureId(factureId)).thenReturn(Optional.of(signature));

        // Act
        SignatureFactureResponse response = signatureService.getSignature(factureId);

        // Assert
        assertNotNull(response);
        assertEquals(factureId, response.factureId());
        verify(signatureRepository, times(1)).findByFactureId(factureId);
    }

    @Test
    void testGetSignature_NotFound() {
        // Arrange
        when(signatureRepository.findByFactureId(factureId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> signatureService.getSignature(factureId));
    }

    private Facture createMockFacture() {
        Facture facture = new Facture();
        facture.setId(UUID.randomUUID());
        facture.setNumero("A-2026-00000001");
        facture.setDateEmission(LocalDate.now());
        facture.setTotalTtc(new BigDecimal("10000.00"));
        facture.setCodeAuthentification("AUTH123456");
        facture.setDateCertification(OffsetDateTime.now());
        return facture;
    }
}
