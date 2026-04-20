package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.LigneFactureResponse;
import bf.gov.dgi.sfe.service.LigneFactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lignes-facture")
@RequiredArgsConstructor
// Expose les lignes de facture pour consultation.
public class LigneFactureController {

    private final LigneFactureService ligneFactureService;

    @GetMapping("/facture/{factureId}")
    // Liste toutes les lignes associees a une facture.
    public ResponseEntity<List<LigneFactureResponse>> listByFacture(@PathVariable UUID factureId) {
        return ResponseEntity.ok(ligneFactureService.listByFacture(factureId));
    }

    // Detail d'une ligne unique.
    @GetMapping("/{id}")
    public ResponseEntity<LigneFactureResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ligneFactureService.get(id));
    }
}
