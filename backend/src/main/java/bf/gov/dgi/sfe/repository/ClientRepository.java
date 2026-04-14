package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Acces base pour les clients.
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
