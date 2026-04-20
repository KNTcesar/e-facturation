package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.McfConnectiviteStatutResponse;
import bf.gov.dgi.sfe.service.McfConnectiviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcf/connectivite")
@RequiredArgsConstructor
// Expose le statut de connectivite MCF a l'Administration pour l'UI.
public class McfConnectiviteController {

    private final McfConnectiviteService mcfConnectiviteService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @GetMapping
    // Retourne le statut courant de connectivite MCF vers l'Administration.
    public ResponseEntity<McfConnectiviteStatutResponse> get() {
        return ResponseEntity.ok(mcfConnectiviteService.getStatut());
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @PostMapping("/refresh")
    // Force une nouvelle verification de connectivite MCF.
    public ResponseEntity<McfConnectiviteStatutResponse> refresh() {
        return ResponseEntity.ok(mcfConnectiviteService.refreshStatut());
    }
}