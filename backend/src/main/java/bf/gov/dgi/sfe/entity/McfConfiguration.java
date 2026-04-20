package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.TypeConnexionMcf;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mcf_configuration")
// Parametrage du port de connexion au MCF.
public class McfConfiguration extends BaseAuditableEntity {

    // Type de connexion (machine locale ou service).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeConnexionMcf typeConnexion;

    // Adresse hote du MCF.
    @Column(nullable = false, length = 120)
    private String host;

    // Port TCP du MCF.
    @Column(nullable = false)
    private int port;

    // Marque la configuration active.
    @Column(nullable = false)
    private boolean actif;
}