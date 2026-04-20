package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.TypeRapportReglementaire;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rapport_reglementaire_executions")
// Trace d'une generation de rapport reglementaire.
public class RapportReglementaireExecution extends BaseAuditableEntity {

    // Type de rapport reglementaire genere.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRapportReglementaire typeRapport;

    // Date de debut de la periode couverte.
    @Column(nullable = false)
    private LocalDate dateDebut;

    // Date de fin de la periode couverte.
    @Column(nullable = false)
    private LocalDate dateFin;

    // Acteur applicatif ayant declenche la generation.
    @Column(nullable = false)
    private String acteur;
}
