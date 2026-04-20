package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "entreprises")
// Donnees legales et fiscales de l'entreprise utilisatrice.
public class Entreprise extends BaseAuditableEntity {

    // Nom/raison sociale de l'entreprise.
    @Column(nullable = false)
    private String nom;

    // IFU unique de l'entreprise.
    @Column(nullable = false, unique = true)
    private String ifu;

    // RCCM unique de l'entreprise.
    @Column(nullable = false, unique = true)
    private String rccm;

    // Regime d'imposition applique.
    @Column(nullable = false)
    private String regimeFiscal;

    // Service des impots de rattachement.
    @Column(nullable = false)
    private String serviceImpotRattachement;

    // Adresse du siege ou adresse fiscale principale.
    @Column(nullable = false)
    private String adresse;

    // Code pays ISO-2 de l'entreprise.
    @Column(nullable = false, length = 2)
    private String paysCode;

    // Ville de l'entreprise.
    @Column(nullable = false)
    private String ville;

    // Telephone principal de l'entreprise.
    @Column
    private String telephone;

    // Email principal de l'entreprise.
    @Column
    private String email;

    // URL du logo entreprise (snapshot utilise en facture).
    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    // Indique si ce profil entreprise est actif.
    @Column(nullable = false)
    private boolean actif;

    // Date d'effet du profil entreprise.
    @Column(nullable = false)
    private LocalDate dateEffet;

    @OneToMany(mappedBy = "entreprise", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Etablissement> etablissements = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CertificatFiscal> certificats = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompteBancaireEntreprise> comptesBancaires = new ArrayList<>();

    public Entreprise(String nom,
                      String ifu,
                      String rccm,
                      String regimeFiscal,
                      String serviceImpotRattachement,
                      String adresse,
                      String paysCode,
                      String ville,
                      String telephone,
                      String email,
                      String logoUrl,
                      boolean actif,
                      LocalDate dateEffet,
                      List<Etablissement> etablissements,
                      List<CertificatFiscal> certificats,
                      List<CompteBancaireEntreprise> comptesBancaires) {
        this.nom = nom;
        this.ifu = ifu;
        this.rccm = rccm;
        this.regimeFiscal = regimeFiscal;
        this.serviceImpotRattachement = serviceImpotRattachement;
        this.adresse = adresse;
        this.paysCode = paysCode;
        this.ville = ville;
        this.telephone = telephone;
        this.email = email;
        this.logoUrl = logoUrl;
        this.actif = actif;
        this.dateEffet = dateEffet;
        this.etablissements = etablissements != null ? etablissements : new ArrayList<>();
        this.certificats = certificats != null ? certificats : new ArrayList<>();
        this.comptesBancaires = comptesBancaires != null ? comptesBancaires : new ArrayList<>();
    }
}
