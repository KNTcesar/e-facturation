package bf.gov.dgi.sfe.entity;

import java.math.BigDecimal;

import bf.gov.dgi.sfe.enums.ModePaiement;
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
@Table(name = "facture_paiements")
// Ventilation des montants de facture par mode de paiement.
public class FacturePaiement extends BaseAuditableEntity {

    // Facture rattachee au paiement.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    // Mode de paiement utilise pour la tranche.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;

    // Montant regle pour ce mode de paiement.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montant;

    // Reference eventuelle du paiement (cheque, transaction, etc.).
    @Column(length = 120)
    private String referencePaiement;
}
