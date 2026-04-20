package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.EntrepriseRequest;
import bf.gov.dgi.sfe.dto.EntrepriseResponse;
import bf.gov.dgi.sfe.dto.UpdateEntrepriseLogoRequest;
import bf.gov.dgi.sfe.service.EntrepriseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/entreprises")
@RequiredArgsConstructor
// Expose le profil fiscal et legal de l'entreprise.
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    // Cree un nouveau profil entreprise fiscal et legal.
    public ResponseEntity<EntrepriseResponse> create(@Valid @RequestBody EntrepriseRequest request) {
        return ResponseEntity.ok(entrepriseService.create(request));
    }

    @GetMapping
    // Liste l'ensemble des profils entreprise enregistres.
    public ResponseEntity<List<EntrepriseResponse>> list() {
        return ResponseEntity.ok(entrepriseService.list());
    }

    // Retourne le profil fiscal actuellement actif.
    @GetMapping("/active")
    public ResponseEntity<EntrepriseResponse> getActive() {
        return ResponseEntity.ok(entrepriseService.getActive());
    }

    // Récupère une entreprise par identifiant.
    @GetMapping("/{id}")
    public ResponseEntity<EntrepriseResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(entrepriseService.get(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    // Met a jour le logo de l'entreprise cible.
    public ResponseEntity<EntrepriseResponse> updateLogo(@PathVariable UUID id, @Valid @RequestBody UpdateEntrepriseLogoRequest request) {
        return ResponseEntity.ok(entrepriseService.updateLogo(id, request.logoUrl()));
    }
}
