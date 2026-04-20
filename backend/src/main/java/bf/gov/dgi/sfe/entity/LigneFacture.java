package bf.gov.dgi.sfe.entity;

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

import bf.gov.dgi.sfe.enums.GroupeTaxation;

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

    // Libelle de la ligne au moment de l'emission.
    @Column(nullable = false)
    private String description;

    // Quantite vendue avec precision 3 decimales.
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantite;

    // Prix unitaire HT de reference.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prixUnitaireHt;

    // Taux de TVA applique a la ligne.
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupeTaxation groupeTaxation;

    // Taxe specifique appliquee a la ligne.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantTaxeSpecifique;

    // Base taxable TVA (HT + taxe specifique).
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal baseTaxableTva;

    // Montant hors taxe de la ligne.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantHt;

    // Montant de TVA de la ligne.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantTva;

    // Montant TTC de la ligne.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantTtc;
}
