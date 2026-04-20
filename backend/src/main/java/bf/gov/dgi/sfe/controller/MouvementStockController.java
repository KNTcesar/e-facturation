package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.MouvementStockResponse;
import bf.gov.dgi.sfe.service.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
// Expose le journal de stock pour suivi et audit.
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @GetMapping
    // Liste les mouvements de stock du plus recent au plus ancien.
    public ResponseEntity<List<MouvementStockResponse>> list() {
        return ResponseEntity.ok(mouvementStockService.list());
    }
}
