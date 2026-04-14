package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.TransmissionSecef;
import bf.gov.dgi.sfe.enums.StatutTransmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Acces base pour les envois fiscaux.
public interface TransmissionSecefRepository extends JpaRepository<TransmissionSecef, UUID> {

	List<TransmissionSecef> findTop20ByStatutInOrderByCreatedAtAsc(List<StatutTransmission> statuts);
}
