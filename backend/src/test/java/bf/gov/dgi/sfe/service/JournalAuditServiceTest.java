package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.AuditChainVerificationResponse;
import bf.gov.dgi.sfe.entity.JournalAudit;
import bf.gov.dgi.sfe.repository.JournalAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalAuditServiceTest {

    @Mock
    private JournalAuditRepository journalAuditRepository;

    @InjectMocks
    private JournalAuditService journalAuditService;

    @Test
    void verifyChain_shouldReturnValidWhenEntriesAreWellChained() {
        JournalAudit first = new JournalAudit();
        first.setSequenceNumber(1L);
        first.setAction("A1");
        first.setEntite("Facture");
        first.setActeur("admin");
        first.setPreviousEntryHash("GENESIS");
        first.setEntryHash(AuditHashing.computeEntryHash(1L, "GENESIS", "A1", "Facture", null, "admin", null, null, null));

        JournalAudit second = new JournalAudit();
        second.setSequenceNumber(2L);
        second.setAction("A2");
        second.setEntite("Facture");
        second.setActeur("admin");
        second.setPreviousEntryHash(first.getEntryHash());
        second.setEntryHash(AuditHashing.computeEntryHash(2L, first.getEntryHash(), "A2", "Facture", null, "admin", null, null, null));

        when(journalAuditRepository.findAllByOrderBySequenceNumberAscCreatedAtAsc()).thenReturn(List.of(first, second));

        AuditChainVerificationResponse response = journalAuditService.verifyChain();

        assertThat(response.valid()).isTrue();
        assertThat(response.totalEntries()).isEqualTo(2);
    }
}
