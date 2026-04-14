package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.SerieFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

// Acces base pour la numerotation des factures.
public interface SerieFactureRepository extends JpaRepository<SerieFacture, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SerieFacture s where s.code = :code and s.exercice = :exercice and s.active = true")
    Optional<SerieFacture> findActiveByCodeAndExerciceForUpdate(@Param("code") String code, @Param("exercice") int exercice);
}
