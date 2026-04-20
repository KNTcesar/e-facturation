package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.MouvementNumeraire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Acces base pour les mouvements de depot/retrait de numeraires.
public interface MouvementNumeraireRepository extends JpaRepository<MouvementNumeraire, UUID> {

    List<MouvementNumeraire> findAllByOrderByDateOperationDescCreatedAtDesc();
}