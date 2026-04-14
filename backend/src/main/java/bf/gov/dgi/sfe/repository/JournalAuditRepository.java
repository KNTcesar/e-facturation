package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Acces base pour les traces d'audit.
public interface JournalAuditRepository extends JpaRepository<JournalAudit, UUID> {

	@Query("select coalesce(max(j.sequenceNumber), 0) from JournalAudit j")
	long findMaxSequenceNumber();

	Optional<JournalAudit> findTopBySequenceNumberIsNotNullOrderBySequenceNumberDesc();

	List<JournalAudit> findAllByOrderBySequenceNumberAscCreatedAtAsc();
}
