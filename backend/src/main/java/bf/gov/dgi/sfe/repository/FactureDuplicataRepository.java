package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.FactureDuplicata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Acces base pour l'historique des duplicatas de facture.
public interface FactureDuplicataRepository extends JpaRepository<FactureDuplicata, UUID> {

	List<FactureDuplicata> findByFactureIdOrderByCreatedAtDesc(UUID factureId);
}