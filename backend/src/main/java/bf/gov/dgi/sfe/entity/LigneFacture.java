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

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lignes_facture")
// Detail d'une ligne de facture liee a un produit.
public class LigneFacture extends BaseAuditableEntity {

    // Facture parente.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    // Produit vendu ou service rendu.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantite;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prixUnitaireHt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantHt;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantTva;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantTtc;
}
