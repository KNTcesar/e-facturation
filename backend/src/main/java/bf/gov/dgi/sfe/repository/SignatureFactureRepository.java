package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.SignatureFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SignatureFactureRepository extends JpaRepository<SignatureFacture, UUID> {
    Optional<SignatureFacture> findByFactureId(UUID factureId);
}
