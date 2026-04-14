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

import bf.gov.dgi.sfe.enums.StatutTransmission;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transmissions_secef")
// Trace d'un envoi fiscal vers le SECeF.
public class TransmissionSecef extends BaseAuditableEntity {

    // Facture associee a la transmission.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @Column(nullable = false)
    private String formatPayload;

    @Column(nullable = false)
    private String payloadHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransmission statut;

    @Column
    private String codeRetour;

    @Column
    private String messageRetour;

    @Column(nullable = false)
    private OffsetDateTime dateEnvoi;

    @Column
    private OffsetDateTime dateAccuse;

    @Column(nullable = false)
    private int retryCount;
}
