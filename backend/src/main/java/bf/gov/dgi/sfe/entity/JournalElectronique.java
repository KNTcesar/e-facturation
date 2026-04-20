package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.TypeDocumentJournalElectronique;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "journal_electronique")
// Journal electronique des contenus de factures et rapports.
public class JournalElectronique extends BaseAuditableEntity {

    // Type de document journalise (facture ou rapport).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocumentJournalElectronique typeDocument;

    // Identifiant du document source.
    @Column(nullable = false)
    private UUID documentId;

    // Reference fonctionnelle du document source.
    @Column(nullable = false, length = 120)
    private String referenceDocument;

    // Code de securite/validation associe (si applicable).
    @Column(columnDefinition = "TEXT")
    private String codeSecefDgi;

    // Contenu JSON complet archive.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenuJson;
}