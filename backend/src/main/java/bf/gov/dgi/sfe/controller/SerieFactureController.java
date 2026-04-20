package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.SerieFactureRequest;
import bf.gov.dgi.sfe.dto.SerieFactureResponse;
import bf.gov.dgi.sfe.service.SerieFactureService;
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
@RequestMapping("/api/series-facture")
@RequiredArgsConstructor
// Expose les series de numerotation.
public class SerieFactureController {

    private final SerieFactureService serieFactureService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    // Cree une serie de numerotation de facture pour un exercice donne.
    public ResponseEntity<SerieFactureResponse> create(@Valid @RequestBody SerieFactureRequest request) {
        return ResponseEntity.ok(serieFactureService.create(request));
    }

    @GetMapping
    // Liste les series de facturation configurees.
    public ResponseEntity<List<SerieFactureResponse>> list() {
        return ResponseEntity.ok(serieFactureService.list());
    }

    // Consultation d'une serie par identifiant.
    @GetMapping("/{id}")
    public ResponseEntity<SerieFactureResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(serieFactureService.get(id));
    }
}
