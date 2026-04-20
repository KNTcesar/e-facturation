package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.ParamLigneCommentaire;
import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParamLigneCommentaireRepository extends JpaRepository<ParamLigneCommentaire, UUID> {

    List<ParamLigneCommentaire> findByActifTrueOrderByCodeAsc();

    Optional<ParamLigneCommentaire> findByCode(CodeLigneCommentaire code);
}
