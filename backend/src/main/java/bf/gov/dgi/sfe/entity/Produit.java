package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "produits")
// Article ou service facturable dans le catalogue.
public class Produit extends BaseAuditableEntity {

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prixUnitaireHt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(nullable = false)
    private String unite;
}
