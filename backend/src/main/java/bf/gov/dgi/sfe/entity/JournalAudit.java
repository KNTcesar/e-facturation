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

    // Numero de sequence garantissant l'ordre immuable des entrees.
    @Column
    private Long sequenceNumber;

    // Hash de l'entree precedente dans la chaine.
    @Column(length = 255)
    private String previousEntryHash;

    // Hash de l'entree courante.
    @Column(length = 255)
    private String entryHash;

    // Action metier realisee.
    @Column(nullable = false)
    private String action;

    // Type d'entite ciblee par l'action.
    @Column(nullable = false)
    private String entite;

    // Identifiant de l'entite ciblee.
    @Column(nullable = false)
    private UUID entiteId;

    // Hash de l'etat avant modification (si applicable).
    @Column
    private String oldHash;

    // Hash de l'etat apres modification (si applicable).
    @Column
    private String newHash;

    // Informations complementaires de trace.
    @Column(length = 1000)
    private String details;

    // Acteur (utilisateur/systeme) ayant declenche l'action.
    @Column(nullable = false)
    private String acteur;
}
