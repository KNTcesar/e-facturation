package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.AnnulationFactureRequest;
import bf.gov.dgi.sfe.dto.CreateFactureRequest;
import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.service.FactureService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
// Expose les operations de facturation au frontend.
public class FactureController {

    private final FactureService factureService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @PostMapping
    public ResponseEntity<FactureResponse> create(@Valid @RequestBody CreateFactureRequest request) {
        return ResponseEntity.ok(factureService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/annuler")
    public ResponseEntity<FactureResponse> annuler(@PathVariable UUID id, @Valid @RequestBody AnnulationFactureRequest request) {
        return ResponseEntity.ok(factureService.annuler(id, request.motif()));
    }

    @GetMapping
    public ResponseEntity<List<FactureResponse>> list() {
        return ResponseEntity.ok(factureService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(factureService.get(id));
    }
}
