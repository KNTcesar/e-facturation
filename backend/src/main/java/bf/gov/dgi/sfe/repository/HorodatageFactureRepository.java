package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.HorodatageFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HorodatageFactureRepository extends JpaRepository<HorodatageFacture, UUID> {
    Optional<HorodatageFacture> findByFactureId(UUID factureId);
}
