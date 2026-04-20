package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
// Base commune pour garder les dates techniques de creation et de mise a jour.
public abstract class BaseAuditableEntity {

    // Identifiant technique UUID genere automatiquement.
    @Id
    @GeneratedValue
    private UUID id;

    // Date de creation persistante de l'enregistrement.
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Date de derniere mise a jour persistante.
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    // Initialise les horodatages techniques lors de l'insertion.
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    // Met a jour l'horodatage technique avant chaque modification.
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
