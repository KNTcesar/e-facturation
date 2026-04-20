package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.McfConfigurationRequest;
import bf.gov.dgi.sfe.dto.McfConfigurationResponse;
import bf.gov.dgi.sfe.service.McfConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcf/configuration")
@RequiredArgsConstructor
// Expose la configuration du port de connexion MCF.
public class McfConfigurationController {

    private final McfConfigurationService mcfConfigurationService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @GetMapping
    // Recupere la configuration MCF active (type, host, port).
    public ResponseEntity<McfConfigurationResponse> get() {
        return ResponseEntity.ok(mcfConfigurationService.getActive());
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PutMapping
    // Met a jour la configuration MCF active.
    public ResponseEntity<McfConfigurationResponse> update(@Valid @RequestBody McfConfigurationRequest request) {
        return ResponseEntity.ok(mcfConfigurationService.update(request));
    }
}