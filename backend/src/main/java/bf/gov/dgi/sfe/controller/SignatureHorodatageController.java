package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.HorodatageFactureResponse;
import bf.gov.dgi.sfe.dto.HorodaterFactureRequest;
import bf.gov.dgi.sfe.dto.SignatureFactureResponse;
import bf.gov.dgi.sfe.dto.SignerFactureRequest;
import bf.gov.dgi.sfe.service.HorodatageFactureService;
import bf.gov.dgi.sfe.service.SignatureFactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
// S4 - Expose les operations de signature et horodatage des factures
public class SignatureHorodatageController {

    private final SignatureFactureService signatureService;
    private final HorodatageFactureService horodatageService;

    // ========== SIGNATURE ==========

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/signer")
    public ResponseEntity<SignatureFactureResponse> signer(@PathVariable UUID id, @Valid @RequestBody SignerFactureRequest request) {
        return ResponseEntity.ok(signatureService.signer(id, request));
    }

    @GetMapping("/{id}/signature")
    public ResponseEntity<SignatureFactureResponse> getSignature(@PathVariable UUID id) {
        return ResponseEntity.ok(signatureService.getSignature(id));
    }

    // ========== HORODATAGE ==========

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/horodater")
    public ResponseEntity<HorodatageFactureResponse> horodater(@PathVariable UUID id, @Valid @RequestBody HorodaterFactureRequest request) {
        return ResponseEntity.ok(horodatageService.horodater(id, request));
    }

    @GetMapping("/{id}/horodatage")
    public ResponseEntity<HorodatageFactureResponse> getHorodatage(@PathVariable UUID id) {
        return ResponseEntity.ok(horodatageService.getHorodatage(id));
    }
}
