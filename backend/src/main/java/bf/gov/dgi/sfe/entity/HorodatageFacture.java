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
@Table(name = "horodatages_facture")
// S4 - Horodatage (timestamping) des factures via autorite de temps
public class HorodatageFacture extends BaseAuditableEntity {

    // Facture horodatee
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false, unique = true)
    private Facture facture;

    // Hash de la signature ou du document
    @Column(nullable = false, length = 64)
    private String hashDocumented;

    // Token d'horodatage retourne par l'autorite (base64)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String tokenHorodatageBase64;

    // Autorite de temps qui a emis le timestamp
    @Column(nullable = false, length = 200)
    private String authoriteTemps;

    // URL de validation du timestamp
    @Column(length = 500)
    private String urlVerification;

    // Date-heure du timestamp (UTC)
    @Column(nullable = false)
    private OffsetDateTime dateHorodatage;

    // Algorithme de hachage employe
    @Column(nullable = false, length = 50)
    private String algorithmeHash;

    // Statut de verification (TSA validation)
    @Column(nullable = false)
    private boolean verifie = false;

    // Fenetre de tolerance en secondes (0 = accepte au moment exact)
    @Column(nullable = false)
    private long toleranceSeconds = 300; // 5 minutes par defaut
}
