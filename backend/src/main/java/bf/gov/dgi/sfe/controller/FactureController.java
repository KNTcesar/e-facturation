package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.AnnulationFactureRequest;
import bf.gov.dgi.sfe.dto.CreateFactureRequest;
import bf.gov.dgi.sfe.dto.FactureDuplicataResponse;
import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    // Cree une facture certifiee avec calcul des montants et generation des elements de securite.
    public ResponseEntity<FactureResponse> create(@Valid @RequestBody CreateFactureRequest request) {
        return ResponseEntity.ok(factureService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/annuler")
    // Annule une facture existante en conservant la trace d'audit.
    public ResponseEntity<FactureResponse> annuler(@PathVariable UUID id, @Valid @RequestBody AnnulationFactureRequest request) {
        return ResponseEntity.ok(factureService.annuler(id, request.motif()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/duplicata")
    // Genere un duplicata immuable de la facture demandee.
    public ResponseEntity<FactureResponse> duplicata(@PathVariable UUID id) {
        return ResponseEntity.ok(factureService.duplicata(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @GetMapping("/{id}/duplicatas")
    // Liste l'historique des duplicatas d'une facture.
    public ResponseEntity<List<FactureDuplicataResponse>> listDuplicatas(@PathVariable UUID id) {
        return ResponseEntity.ok(factureService.listDuplicatas(id));
    }

    @GetMapping
    // Retourne la liste des factures.
    public ResponseEntity<List<FactureResponse>> list() {
        return ResponseEntity.ok(factureService.list());
    }

    @GetMapping("/{id}")
    // Retourne le detail d'une facture par son identifiant.
    public ResponseEntity<FactureResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(factureService.get(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE','CAISSIER')")
    @GetMapping(value = "/{id}/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    // Genere l'image PNG du QR code associe a la facture.
    public ResponseEntity<byte[]> qrCode(@PathVariable UUID id, @RequestParam(defaultValue = "320") int size) {
        return ResponseEntity.ok(factureService.getQrCodePng(id, size));
    }
}
