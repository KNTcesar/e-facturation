package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Acces base pour le profil fiscal de l'entreprise.
public interface EntrepriseRepository extends JpaRepository<Entreprise, UUID> {
	Optional<Entreprise> findFirstByActifTrueOrderByDateEffetDesc();

	List<Entreprise> findByActifTrue();
}
