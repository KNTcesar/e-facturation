package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.HorodatageFactureResponse;
import bf.gov.dgi.sfe.dto.HorodaterFactureRequest;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.HorodatageFacture;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.HorodatageFactureRepository;
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
// S4 - Tests d'horodatage des factures
public class HorodatageFactureServiceTest {
    @Mock
    private HorodatageFactureRepository horodatageRepository;

    @Mock
    private FactureRepository factureRepository;

    @InjectMocks
    private HorodatageFactureService horodatageService;

    private UUID factureId;
    private Facture mockFacture;

    @BeforeEach
    void setUp() {
        factureId = UUID.randomUUID();
        mockFacture = createMockFacture();
    }

    @Test
    void testHorodater_Success() {
        // Arrange
        String tokenBase64 = "MIIEkQIBAAKCAQEA2qWez...";
        String authorite = "TSA-BURKINA-FASO";
        String algo = "SHA-256";

        mockFacture.setId(factureId); // Ensure facture has correct ID
        when(factureRepository.findById(factureId)).thenReturn(Optional.of(mockFacture));
        when(horodatageRepository.save(any(HorodatageFacture.class)))
                .thenAnswer(inv -> {
                    HorodatageFacture h = inv.getArgument(0);
                    h.setId(UUID.randomUUID());
                    return h;
                });

        HorodaterFactureRequest request = new HorodaterFactureRequest(tokenBase64, authorite, algo);

        // Act
        HorodatageFactureResponse response = horodatageService.horodater(factureId, request);

        // Assert
        assertNotNull(response);
        assertEquals(factureId, response.factureId());
        assertEquals(authorite, response.authoriteTemps());
        assertTrue(response.verifie());
        verify(factureRepository, times(1)).findById(factureId);
        verify(horodatageRepository, times(1)).save(any(HorodatageFacture.class));
    }

    @Test
    void testGetHorodatage_Success() {
        // Arrange
        mockFacture.setId(factureId); // Ensure facture has correct ID
        HorodatageFacture horodatage = new HorodatageFacture();
        horodatage.setId(UUID.randomUUID());
        horodatage.setFacture(mockFacture);
        horodatage.setAuthoriteTemps("TSA-BURKINA-FASO");
        horodatage.setAlgorithmeHash("SHA-256");
        horodatage.setVerifie(true);

        when(horodatageRepository.findByFactureId(factureId)).thenReturn(Optional.of(horodatage));

        // Act
        HorodatageFactureResponse response = horodatageService.getHorodatage(factureId);

        // Assert
        assertNotNull(response);
        assertEquals(factureId, response.factureId());
        verify(horodatageRepository, times(1)).findByFactureId(factureId);
    }

    @Test
    void testGetHorodatage_NotFound() {
        // Arrange
        when(horodatageRepository.findByFactureId(factureId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> horodatageService.getHorodatage(factureId));
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
