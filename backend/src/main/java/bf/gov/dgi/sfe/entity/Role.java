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
@Table(name = "roles")
// Catalogue des roles applicatifs pour la securite et les droits.
public class Role extends BaseAuditableEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;
}
