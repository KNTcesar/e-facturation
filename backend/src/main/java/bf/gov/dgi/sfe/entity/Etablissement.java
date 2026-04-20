package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "etablissements")
// Etablissement rattache a une entreprise fiscale.
public class Etablissement extends BaseAuditableEntity {

    // Entreprise a laquelle appartient l'etablissement.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Code interne unique de l'etablissement dans l'entreprise.
    @Column(nullable = false)
    private String code;

    // Nom commercial de l'etablissement.
    @Column(nullable = false)
    private String nom;

    // Adresse physique de l'etablissement.
    @Column(nullable = false)
    private String adresse;

    // Ville de rattachement de l'etablissement.
    @Column(nullable = false)
    private String ville;

    // Indique si cet etablissement est le principal.
    @Column(nullable = false)
    private boolean principal;

    // Etat actif/inactif de l'etablissement.
    @Column(nullable = false)
    private boolean actif;
}
