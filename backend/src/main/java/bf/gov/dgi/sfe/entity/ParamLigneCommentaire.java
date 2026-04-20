package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "param_lignes_commentaire")
// Parametrage des 8 lignes de commentaires A..H.
public class ParamLigneCommentaire extends BaseAuditableEntity {

    // Code unique de la ligne commentaire (A..H).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CodeLigneCommentaire code;

    // Libelle de la ligne commentaire.
    @Column(nullable = false, length = 120)
    private String etiquette;

    // Valeur par defaut de la ligne commentaire.
    @Column(nullable = false, length = 500)
    private String contenuParDefaut;

    // Active/desactive la ligne dans le parametrage.
    @Column(nullable = false)
    private boolean actif;
}
