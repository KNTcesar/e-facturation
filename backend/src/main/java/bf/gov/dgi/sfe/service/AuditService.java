package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.JournalAudit;
import bf.gov.dgi.sfe.repository.JournalAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final JournalAuditRepository journalAuditRepository;

    @Transactional
    public void trace(String action, String entite, UUID entiteId, String acteur) {
        trace(action, entite, entiteId, acteur, null, null, null);
    }

    @Transactional
    public void trace(String action,
                      String entite,
                      UUID entiteId,
                      String acteur,
                      String oldHash,
                      String newHash,
                      String details) {
        long sequenceNumber = journalAuditRepository.findMaxSequenceNumber() + 1;
        String previousEntryHash = journalAuditRepository.findTopBySequenceNumberIsNotNullOrderBySequenceNumberDesc()
                .map(JournalAudit::getEntryHash)
                .orElse("GENESIS");
        String entryHash = AuditHashing.computeEntryHash(
                sequenceNumber,
                previousEntryHash,
                action,
                entite,
                entiteId,
                acteur,
                oldHash,
                newHash,
                details
        );

        JournalAudit entry = new JournalAudit();
        entry.setSequenceNumber(sequenceNumber);
        entry.setPreviousEntryHash(previousEntryHash);
        entry.setEntryHash(entryHash);
        entry.setAction(action);
        entry.setEntite(entite);
        entry.setEntiteId(entiteId);
        entry.setOldHash(oldHash);
        entry.setNewHash(newHash);
        entry.setDetails(details);
        entry.setActeur(acteur);
        journalAuditRepository.save(entry);
    }
}
