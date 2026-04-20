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

    // Code de serie visible sur les factures.
    @Column(nullable = false)
    private String code;

    // Exercice fiscal de la serie.
    @Column(nullable = false)
    private int exercice;

    // Prochain numero a attribuer.
    @Column(nullable = false)
    private long prochainNumero;

    // Indique si la serie est exploitable.
    @Column(nullable = false)
    private boolean active;
}
