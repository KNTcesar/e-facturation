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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "facture_duplicatas")
// Trace persistante d'un duplicata de facture.
public class FactureDuplicata extends BaseAuditableEntity {

    // Facture source du duplicata.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    // Numero de la facture dupliquee.
    @Column(nullable = false, length = 80)
    private String numeroFacture;

    // Date d'emission de la facture dupliquee.
    @Column(nullable = false)
    private LocalDate dateFacture;

    // Utilisateur ayant demande le duplicata.
    @Column(nullable = false, length = 120)
    private String demandeur;

    // Motif fonctionnel du duplicata.
    @Column(length = 255)
    private String motif;

    // Snapshot JSON de la facture au moment du duplicata.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String snapshotJson;

    // Empreinte de controle du snapshot.
    @Column(nullable = false, length = 64)
    private String empreinteSnapshot;
}