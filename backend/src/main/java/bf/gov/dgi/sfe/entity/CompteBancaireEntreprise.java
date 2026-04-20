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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "entreprise_comptes_bancaires")
// Reference d'un compte bancaire configure pour l'entreprise.
public class CompteBancaireEntreprise extends BaseAuditableEntity {

    // Entreprise proprietaire du compte bancaire.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Reference bancaire (RIB/IBAN/numero compte).
    @Column(nullable = false, length = 255)
    private String referenceCompte;

    // Nom de la banque domiciliaire.
    @Column(length = 180)
    private String banque;

    // Indique si ce compte est actif pour emission.
    @Column(nullable = false)
    private boolean actif;
}