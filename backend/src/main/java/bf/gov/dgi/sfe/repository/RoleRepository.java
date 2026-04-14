package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Acces base pour les roles applicatifs.
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCode(String code);
}
