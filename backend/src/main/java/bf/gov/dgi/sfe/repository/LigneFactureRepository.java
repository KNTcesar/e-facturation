package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.LigneFacture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneFactureRepository extends JpaRepository<LigneFacture, UUID> {
    List<LigneFacture> findByFactureId(UUID factureId);
}
