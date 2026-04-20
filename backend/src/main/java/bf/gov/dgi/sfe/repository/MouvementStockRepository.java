package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Acces base pour le journal de mouvements de stock.
public interface MouvementStockRepository extends JpaRepository<MouvementStock, UUID> {

    List<MouvementStock> findAllByOrderByCreatedAtDesc();
}
