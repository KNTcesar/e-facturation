package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.UtilisateurActivationRequest;
import bf.gov.dgi.sfe.dto.UtilisateurPasswordResetRequest;
import bf.gov.dgi.sfe.dto.UtilisateurRequest;
import bf.gov.dgi.sfe.dto.UtilisateurResponse;
import bf.gov.dgi.sfe.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
// Expose la gestion des comptes utilisateurs.
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UtilisateurResponse> create(@Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(utilisateurService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> list() {
        return ResponseEntity.ok(utilisateurService.list());
    }

    // Consultation d'un utilisateur précis.
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(utilisateurService.get(id));
    }

    // Active ou désactive un compte utilisateur.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activation")
    public ResponseEntity<UtilisateurResponse> setActivation(@PathVariable UUID id,
                                                             @RequestBody UtilisateurActivationRequest request) {
        return ResponseEntity.ok(utilisateurService.setActivation(id, request.actif()));
    }

    // Réinitialise le mot de passe d'un utilisateur.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/password")
    public ResponseEntity<UtilisateurResponse> resetPassword(@PathVariable UUID id,
                                                             @Valid @RequestBody UtilisateurPasswordResetRequest request) {
        return ResponseEntity.ok(utilisateurService.resetPassword(id, request.newPassword()));
    }

    // Supprime un utilisateur.
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
