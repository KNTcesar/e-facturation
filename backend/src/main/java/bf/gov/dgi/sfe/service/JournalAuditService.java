package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.AuditChainVerificationResponse;
import bf.gov.dgi.sfe.dto.JournalAuditResponse;
import bf.gov.dgi.sfe.entity.JournalAudit;
import bf.gov.dgi.sfe.repository.JournalAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Lecture du journal d'audit immuable.
public class JournalAuditService {

    private final JournalAuditRepository journalAuditRepository;

    @Transactional(readOnly = true)
    public List<JournalAuditResponse> list() {
        return journalAuditRepository.findAllByOrderBySequenceNumberAscCreatedAtAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public JournalAuditResponse get(UUID id) {
        return toResponse(journalAuditRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Audit introuvable")));
    }

    @Transactional(readOnly = true)
    public AuditChainVerificationResponse verifyChain() {
        List<JournalAudit> entries = journalAuditRepository.findAllByOrderBySequenceNumberAscCreatedAtAsc();
        if (entries.isEmpty()) {
            return new AuditChainVerificationResponse(true, 0, null, "Journal d'audit vide");
        }

        JournalAudit previous = null;
        for (JournalAudit current : entries) {
            if (current.getSequenceNumber() == null || current.getEntryHash() == null || current.getPreviousEntryHash() == null) {
                return new AuditChainVerificationResponse(false, entries.size(), current.getId(), "Entree audit non chainee detectee");
            }

            String expectedPrevious = previous == null ? "GENESIS" : previous.getEntryHash();
            if (!expectedPrevious.equals(current.getPreviousEntryHash())) {
                return new AuditChainVerificationResponse(false, entries.size(), current.getId(), "Rupture de chainage detectee");
            }

            String expectedHash = AuditHashing.computeEntryHash(
                    current.getSequenceNumber(),
                    current.getPreviousEntryHash(),
                    current.getAction(),
                    current.getEntite(),
                    current.getEntiteId(),
                    current.getActeur(),
                    current.getOldHash(),
                    current.getNewHash(),
                    current.getDetails()
            );
            if (!expectedHash.equals(current.getEntryHash())) {
                return new AuditChainVerificationResponse(false, entries.size(), current.getId(), "Hash d'entree invalide");
            }
            previous = current;
        }
        return new AuditChainVerificationResponse(true, entries.size(), null, "Chainage audit valide");
    }

    private JournalAuditResponse toResponse(JournalAudit audit) {
        return new JournalAuditResponse(
                audit.getId(),
                audit.getSequenceNumber(),
                audit.getPreviousEntryHash(),
                audit.getEntryHash(),
                audit.getAction(),
                audit.getEntite(),
                audit.getEntiteId(),
                audit.getOldHash(),
                audit.getNewHash(),
                audit.getDetails(),
                audit.getActeur(),
                audit.getCreatedAt()
        );
    }
}
