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

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String ifu;

    @Column(nullable = false, unique = true)
    private String rccm;

    @Column(nullable = false)
    private String regimeFiscal;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false, length = 2)
    private String paysCode;

    @Column(nullable = false)
    private String ville;

    @Column
    private String telephone;

    @Column
    private String email;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    @Column(nullable = false)
    private boolean actif;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @OneToMany(mappedBy = "entreprise", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Etablissement> etablissements = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CertificatFiscal> certificats = new ArrayList<>();

    public Entreprise(String nom,
                      String ifu,
                      String rccm,
                      String regimeFiscal,
                      String adresse,
                      String paysCode,
                      String ville,
                      String telephone,
                      String email,
                      String logoUrl,
                      boolean actif,
                      LocalDate dateEffet,
                      List<Etablissement> etablissements,
                      List<CertificatFiscal> certificats) {
        this.nom = nom;
        this.ifu = ifu;
        this.rccm = rccm;
        this.regimeFiscal = regimeFiscal;
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
    }
}
