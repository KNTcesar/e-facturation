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

    // Format technique de la charge utile (JSON/XML).
    @Column(nullable = false)
    private String formatPayload;

    // Empreinte d'integrite du payload.
    @Column(nullable = false)
    private String payloadHash;

    // Charge utile effectivement transmise au MCF/SECeF.
    @Column(columnDefinition = "TEXT")
    private String payloadData;

    // Statut courant du cycle de transmission.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransmission statut;

    // Code retour fourni par le SECeF.
    @Column
    private String codeRetour;

    // Message retour fourni par le SECeF.
    @Column
    private String messageRetour;

    // Date/heure d'envoi effectif.
    @Column(nullable = false)
    private OffsetDateTime dateEnvoi;

    // Date/heure d'accuse ou confirmation.
    @Column
    private OffsetDateTime dateAccuse;

    // Nombre de tentatives d'envoi effectuees.
    @Column(nullable = false)
    private int retryCount;
}
