package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import bf.gov.dgi.sfe.enums.TypeClient;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients")
// Tiers facture qui recoit les documents emis.
public class Client extends BaseAuditableEntity {

    // Type reglementaire du client (CC/PM/PP/PC).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeClient typeClient;

    // Nom ou raison sociale du client.
    @Column(nullable = false)
    private String nom;

    // IFU du client (obligatoire selon le type).
    @Column(unique = true)
    private String ifu;

    // RCCM du client (obligatoire selon le type).
    @Column(unique = true)
    private String rccm;

    // Adresse de facturation du client.
    @Column(nullable = false)
    private String adresse;

    // Telephone de contact du client.
    @Column
    private String telephone;

    // Email de contact du client.
    @Column
    private String email;
}
