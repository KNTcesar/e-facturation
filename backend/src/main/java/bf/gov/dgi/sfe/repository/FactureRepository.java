package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Acces base pour les factures.
public interface FactureRepository extends JpaRepository<Facture, UUID> {
}
