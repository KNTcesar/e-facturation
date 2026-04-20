package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.RapportReglementaireExecution;
import bf.gov.dgi.sfe.enums.TypeRapportReglementaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RapportReglementaireExecutionRepository extends JpaRepository<RapportReglementaireExecution, UUID> {

    Optional<RapportReglementaireExecution> findTopByTypeRapportOrderByDateFinDesc(TypeRapportReglementaire typeRapport);
}
