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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "certificats_fiscaux")
// Certificat fiscal utilise pour signer et authentifier les emissions.
public class CertificatFiscal extends BaseAuditableEntity {

    // Entreprise proprietaire du certificat fiscal.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Numero de serie unique du certificat.
    @Column(nullable = false, unique = true)
    private String numeroSerie;

    // ISF attribue par l'administration pour le modele SFE approuve.
    @Column(nullable = false, unique = true)
    private String numeroIsf;

    // Autorite ayant emis le certificat.
    @Column(nullable = false)
    private String autoriteEmission;

    // Date de debut de validite du certificat.
    @Column(nullable = false)
    private LocalDate dateDebutValidite;

    // Date de fin de validite du certificat.
    @Column(nullable = false)
    private LocalDate dateFinValidite;

    // Etat d'activation du certificat.
    @Column(nullable = false)
    private boolean actif;
}
