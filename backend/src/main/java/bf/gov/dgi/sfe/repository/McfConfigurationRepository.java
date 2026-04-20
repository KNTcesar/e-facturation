package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.McfConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Acces base pour la configuration de port MCF.
public interface McfConfigurationRepository extends JpaRepository<McfConfiguration, UUID> {

    Optional<McfConfiguration> findFirstByActifTrueOrderByUpdatedAtDesc();
}