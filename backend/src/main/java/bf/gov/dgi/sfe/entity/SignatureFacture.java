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

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "signatures_facture")
// S4 - Signature des factures avec certificat numerique
public class SignatureFacture extends BaseAuditableEntity {

    // Facture signee
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false, unique = true)
    private Facture facture;

    // Donnees signees (hash de la facture)
    @Column(nullable = false, length = 64)
    private String dataBrute;

    // Signature numerique (base64)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String signatureBase64;

    // Algorithme utilise (RSA-SHA256, etc)
    @Column(nullable = false, length = 100)
    private String algorithme;

    // Certificat X.509 en base64
    @Column(nullable = false, columnDefinition = "TEXT")
    private String certificatBase64;

    // Date de signature
    @Column(nullable = false)
    private OffsetDateTime dateSignature;

    // Fingerprint du certificat (SHA-256)
    @Column(nullable = false, length = 64)
    private String certificatFingerprint;

    // Statut de verification
    @Column(nullable = false)
    private boolean verifie = false;
}
