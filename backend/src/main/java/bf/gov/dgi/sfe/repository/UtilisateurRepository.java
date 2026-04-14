package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Acces base pour les comptes utilisateurs.
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
    Optional<Utilisateur> findByUsername(String username);
}
