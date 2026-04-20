package bf.gov.dgi.sfe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.TypeFacture;
import bf.gov.dgi.sfe.enums.NatureFactureAvoir;
import bf.gov.dgi.sfe.enums.GroupePsvb;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "factures")
// Document fiscal principal genere par le systeme.
public class Facture extends BaseAuditableEntity {

    // Type de facture selon nomenclature DGI (FV/FT/FA/EV/ET/EA).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeFacture typeFacture;

    // Mode de prix unitaire applique sur la facture (HT/TTC).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePrixArticle modePrixUnitaire;

    // Nature obligatoire pour les factures d'avoir.
    @Enumerated(EnumType.STRING)
    @Column
    private NatureFactureAvoir natureFactureAvoir;

    // Reference de facture originale dans le cadre d'un avoir.
    @Column(length = 50)
    private String referenceFactureOriginale;

    // Numero unique attribue par serie.
    @Column(nullable = false, unique = true)
    private String numero;

    // Date de creation de la facture.
    @Column(nullable = false)
    private LocalDate dateEmission;

    // Exercice fiscal de rattachement de la facture.
    @Column(nullable = false)
    private int exercice;

    // Etat courant du document fiscal.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    // Totaux calculees a partir des lignes.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalHt;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalTva;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalTtc;

    @Enumerated(EnumType.STRING)
    @Column
    private GroupePsvb groupePsvb;

    @Column(precision = 5, scale = 2)
    private BigDecimal tauxPsvb;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montantPsvb;

    // Code d'authentification exigé pour la verification.
    @Column(nullable = false, unique = true)
    private String codeAuthentification;

    // Donnees embarquees dans le QR code.
    @Column(nullable = false, length = 2000)
    private String qrPayload;

    // Date de certification finale.
    @Column(nullable = false)
    private OffsetDateTime dateCertification;

    // Motif saisi en cas d'annulation legale.
    @Column(length = 500)
    private String motifAnnulation;

    // Reference contractuelle du marche/publication.
    @Column(length = 120)
    private String referenceMarche;

    // Description de l'objet contractuel facture.
    @Column(length = 255)
    private String objetMarche;

    // Date de contractualisation du marche.
    @Column
    private LocalDate dateMarche;

    // Fenetre d'execution contractuelle.
    @Column
    private LocalDate dateDebutExecution;

    @Column
    private LocalDate dateFinExecution;

    // Logo de l'entreprise emettrice au moment de la facture.
    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    // Client destinataire de la facture.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Serie utilisee pour la numerotation.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_facture_id", nullable = false)
    private SerieFacture serieFacture;

    // Lignes detaillees de la facture.
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    // Ventilation des paiements de la facture.
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturePaiement> paiements = new ArrayList<>();

    // Lignes de commentaires A..H affichees sur la facture.
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FactureCommentaire> commentaires = new ArrayList<>();
}
