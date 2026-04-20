package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

// Acces base pour les produits et services.
public interface ProduitRepository extends JpaRepository<Produit, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Produit p where p.id = :id")
	Optional<Produit> findByIdForUpdate(@Param("id") UUID id);
}
