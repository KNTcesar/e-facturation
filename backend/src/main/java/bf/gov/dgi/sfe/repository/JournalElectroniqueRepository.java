package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.JournalElectronique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Acces base du journal electronique.
public interface JournalElectroniqueRepository extends JpaRepository<JournalElectronique, UUID> {

    List<JournalElectronique> findAllByOrderByCreatedAtDesc();
}