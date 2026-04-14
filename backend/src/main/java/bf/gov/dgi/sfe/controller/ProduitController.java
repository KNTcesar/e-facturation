package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.ProduitRequest;
import bf.gov.dgi.sfe.dto.ProduitResponse;
import bf.gov.dgi.sfe.service.ProduitService;
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
@RequestMapping("/api/produits")
@RequiredArgsConstructor
// Expose la gestion des produits.
public class ProduitController {

    private final ProduitService produitService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping
    public ResponseEntity<ProduitResponse> create(@Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(produitService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProduitResponse>> list() {
        return ResponseEntity.ok(produitService.list());
    }
}
