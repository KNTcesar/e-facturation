package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "journal_audit")
// Journal immuable des actions sensibles.
public class JournalAudit extends BaseAuditableEntity {

    @Column
    private Long sequenceNumber;

    @Column(length = 255)
    private String previousEntryHash;

    @Column(length = 255)
    private String entryHash;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entite;

    @Column(nullable = false)
    private UUID entiteId;

    @Column
    private String oldHash;

    @Column
    private String newHash;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private String acteur;
}
