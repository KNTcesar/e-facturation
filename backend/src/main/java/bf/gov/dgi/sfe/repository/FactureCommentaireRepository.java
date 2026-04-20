package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.FactureCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FactureCommentaireRepository extends JpaRepository<FactureCommentaire, UUID> {
}
