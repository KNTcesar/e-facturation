package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.MouvementNumeraireRequest;
import bf.gov.dgi.sfe.dto.MouvementNumeraireResponse;
import bf.gov.dgi.sfe.service.MouvementNumeraireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-numeraire")
@RequiredArgsConstructor
// Expose l'enregistrement des depots et retraits de numeraires.
public class MouvementNumeraireController {

    private final MouvementNumeraireService mouvementNumeraireService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @PostMapping
    // Enregistre un depot ou un retrait de numeraires.
    public ResponseEntity<MouvementNumeraireResponse> create(@Valid @RequestBody MouvementNumeraireRequest request) {
        return ResponseEntity.ok(mouvementNumeraireService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @GetMapping
    // Liste les mouvements de numeraires recents.
    public ResponseEntity<List<MouvementNumeraireResponse>> list() {
        return ResponseEntity.ok(mouvementNumeraireService.list());
    }
}