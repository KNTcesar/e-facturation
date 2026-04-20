package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.TypeMouvementStock;
import bf.gov.dgi.sfe.enums.TypeOrigineMouvementStock;
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

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mouvements_stock")
// Journal des entrees/sorties de stock pour audit et tracabilite.
public class MouvementStock extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TypeMouvementStock typeMouvement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TypeOrigineMouvementStock origineType;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantite;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal stockAvant;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal stockApres;

    @Column(length = 120)
    private String origineReference;

    @Column(length = 255)
    private String motif;

    @Column(nullable = false, length = 120)
    private String acteur;
}
