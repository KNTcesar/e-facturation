package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Acces lecture/ecriture des etablissements fiscaux.
public interface EtablissementRepository extends JpaRepository<Etablissement, UUID> {
}
