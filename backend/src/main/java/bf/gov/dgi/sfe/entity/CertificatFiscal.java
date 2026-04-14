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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(nullable = false, unique = true)
    private String numeroSerie;

    @Column(nullable = false)
    private String autoriteEmission;

    @Column(nullable = false)
    private LocalDate dateDebutValidite;

    @Column(nullable = false)
    private LocalDate dateFinValidite;

    @Column(nullable = false)
    private boolean actif;
}
