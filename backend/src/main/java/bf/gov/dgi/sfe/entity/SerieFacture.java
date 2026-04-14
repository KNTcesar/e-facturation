package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "series_facture")
// Source de numerotation des factures par serie.
public class SerieFacture extends BaseAuditableEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int exercice;

    @Column(nullable = false)
    private long prochainNumero;

    @Column(nullable = false)
    private boolean active;
}
