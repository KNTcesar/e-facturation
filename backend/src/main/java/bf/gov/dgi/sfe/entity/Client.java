package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients")
// Tiers facture qui recoit les documents emis.
public class Client extends BaseAuditableEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String ifu;

    @Column(nullable = false)
    private String adresse;

    @Column
    private String telephone;

    @Column
    private String email;
}
