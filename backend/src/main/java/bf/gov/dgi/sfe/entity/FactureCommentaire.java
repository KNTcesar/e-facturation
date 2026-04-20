package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.CodeLigneCommentaire;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "facture_commentaires")
// Snapshot des commentaires affiches sur une facture.
public class FactureCommentaire extends BaseAuditableEntity {

    // Facture cible de la ligne de commentaire.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    // Code reglementaire de commentaire (A..H).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeLigneCommentaire code;

    // Libelle affiche pour la ligne commentaire.
    @Column(nullable = false, length = 120)
    private String etiquette;

    // Contenu texte de la ligne commentaire.
    @Column(nullable = false, length = 500)
    private String contenu;

    // Ordre d'affichage de la ligne sur la facture.
    @Column(nullable = false)
    private int ordreAffichage;
}
