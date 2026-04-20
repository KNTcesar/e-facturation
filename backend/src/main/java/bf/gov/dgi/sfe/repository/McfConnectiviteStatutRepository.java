package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.McfConnectiviteStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Acces base du statut de connectivite MCF.
public interface McfConnectiviteStatutRepository extends JpaRepository<McfConnectiviteStatut, UUID> {

    Optional<McfConnectiviteStatut> findFirstByActifTrueOrderByUpdatedAtDesc();
}