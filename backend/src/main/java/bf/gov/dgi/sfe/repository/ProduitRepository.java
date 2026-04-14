package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Acces base pour les produits et services.
public interface ProduitRepository extends JpaRepository<Produit, UUID> {
}
