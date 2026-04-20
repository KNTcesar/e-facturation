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

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mcf_connectivite_statut")
// Statut quotidien de connectivite MCF vers l'Administration.
public class McfConnectiviteStatut extends BaseAuditableEntity {

    // Configuration MCF utilisee pour la verification.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mcf_configuration_id", nullable = false)
    private McfConfiguration mcfConfiguration;

    // Indique si le MCF est considere connecte a l'Administration.
    @Column(nullable = false)
    private boolean connecteAdministration;

    // Date de derniere connexion constatee.
    @Column
    private OffsetDateTime dateDerniereConnexionAdministration;

    // Date de derniere verification executee.
    @Column(nullable = false)
    private OffsetDateTime dateDerniereVerification;

    // Nombre de jours depuis la derniere connexion.
    @Column(nullable = false)
    private int joursDepuisDerniereConnexion;

    // Indique si une alerte doit etre affichee.
    @Column(nullable = false)
    private boolean alerteActive;

    // Message d'alerte explicatif pour l'UI.
    @Column(length = 500)
    private String messageAlerte;

    // Statut actif de l'enregistrement courant.
    @Column(nullable = false)
    private boolean actif;
}