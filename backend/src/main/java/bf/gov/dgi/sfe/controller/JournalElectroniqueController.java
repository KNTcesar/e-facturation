package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.JournalElectroniqueResponse;
import bf.gov.dgi.sfe.service.JournalElectroniqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal-electronique")
@RequiredArgsConstructor
// Consultation du journal electronique (factures et rapports).
public class JournalElectroniqueController {

    private final JournalElectroniqueService journalElectroniqueService;

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @GetMapping
    // Liste les entrees du journal electronique (factures et rapports).
    public ResponseEntity<List<JournalElectroniqueResponse>> list() {
        return ResponseEntity.ok(journalElectroniqueService.list());
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @GetMapping("/{id}")
    // Retourne une entree precise du journal electronique.
    public ResponseEntity<JournalElectroniqueResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(journalElectroniqueService.get(id));
    }
}