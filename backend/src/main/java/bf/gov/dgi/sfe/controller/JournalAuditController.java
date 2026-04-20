package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.AuditChainVerificationResponse;
import bf.gov.dgi.sfe.dto.JournalAuditResponse;
import bf.gov.dgi.sfe.service.JournalAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal-audit")
@RequiredArgsConstructor
// Expose le journal d'audit aux profils autorises.
public class JournalAuditController {

    private final JournalAuditService journalAuditService;

    @GetMapping
    // Liste les enregistrements du journal d'audit.
    public ResponseEntity<List<JournalAuditResponse>> list() {
        return ResponseEntity.ok(journalAuditService.list());
    }

    @GetMapping("/verify")
    // Verifie l'integrite de la chaine de hash du journal d'audit.
    public ResponseEntity<AuditChainVerificationResponse> verify() {
        return ResponseEntity.ok(journalAuditService.verifyChain());
    }

    // Consultation d'une entree d'audit particuliere.
    @GetMapping("/{id}")
    public ResponseEntity<JournalAuditResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(journalAuditService.get(id));
    }
}
