package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import bf.gov.dgi.sfe.enums.TypeArticle;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "produits")
// Article ou service facturable dans le catalogue.
public class Produit extends BaseAuditableEntity {

    // Reference unique article/service.
    @Column(nullable = false, unique = true)
    private String reference;

    // Designation commerciale du produit.
    @Column(nullable = false, length = 255)
    private String designation;

    // Type metier du produit (bien/service).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeArticle typeArticle;

    // Mode de saisie du prix unitaire (HT/TTC).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePrixArticle modePrixArticle;

    // Prix unitaire hors taxe.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prixUnitaireHt;

    // Prix unitaire toutes taxes comprises.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prixUnitaireTtc;

    // Taux de TVA applique au produit.
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    // Groupe de taxation DGI associe au produit.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupeTaxation groupeTaxation;

    // Montant unitaire de taxe specifique applicable a l'article.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal taxeSpecifiqueUnitaire;

    // Unite de mesure du produit.
    @Column(nullable = false)
    private String unite;

    // Quantite disponible en stock logique.
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantiteDisponible;
}
