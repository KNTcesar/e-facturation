package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.entity.JournalAudit;
import bf.gov.dgi.sfe.repository.JournalAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private JournalAuditRepository journalAuditRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void trace_shouldChainEntriesWithSequence() {
        UUID entiteId = UUID.randomUUID();

        when(journalAuditRepository.findMaxSequenceNumber()).thenReturn(0L);
        when(journalAuditRepository.findTopBySequenceNumberIsNotNullOrderBySequenceNumberDesc()).thenReturn(Optional.empty());
        when(journalAuditRepository.save(any(JournalAudit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        auditService.trace("CREATE_FACTURE", "Facture", entiteId, "admin");

        ArgumentCaptor<JournalAudit> firstCaptor = ArgumentCaptor.forClass(JournalAudit.class);
        verify(journalAuditRepository).save(firstCaptor.capture());
        JournalAudit first = firstCaptor.getValue();

        when(journalAuditRepository.findMaxSequenceNumber()).thenReturn(1L);
        when(journalAuditRepository.findTopBySequenceNumberIsNotNullOrderBySequenceNumberDesc()).thenReturn(Optional.of(first));

        auditService.trace("ANNULER_FACTURE", "Facture", entiteId, "admin", "old", "new", "motif");

        ArgumentCaptor<JournalAudit> secondCaptor = ArgumentCaptor.forClass(JournalAudit.class);
        verify(journalAuditRepository, org.mockito.Mockito.times(2)).save(secondCaptor.capture());
        JournalAudit second = secondCaptor.getAllValues().get(1);

        assertThat(first.getSequenceNumber()).isEqualTo(1L);
        assertThat(first.getPreviousEntryHash()).isEqualTo("GENESIS");
        assertThat(first.getEntryHash()).isNotBlank();

        assertThat(second.getSequenceNumber()).isEqualTo(2L);
        assertThat(second.getPreviousEntryHash()).isEqualTo(first.getEntryHash());
        assertThat(second.getEntryHash()).isNotBlank();
        assertThat(second.getOldHash()).isEqualTo("old");
        assertThat(second.getNewHash()).isEqualTo("new");
        assertThat(second.getDetails()).isEqualTo("motif");
    }
}
