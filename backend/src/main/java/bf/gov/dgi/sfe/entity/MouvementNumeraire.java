package bf.gov.dgi.sfe.entity;

import bf.gov.dgi.sfe.enums.TypeMouvementNumeraire;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mouvements_numeraire")
// Mouvement de depot ou retrait de numeraires.
public class MouvementNumeraire extends BaseAuditableEntity {

    // Nature du mouvement (depot ou retrait).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvementNumeraire typeMouvement;

    // Montant du mouvement.
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montant;

    // Date operationnelle du mouvement.
    @Column(nullable = false)
    private LocalDate dateOperation;

    // Motif du depot/retrait.
    @Column(length = 255)
    private String motif;

    // Acteur ayant enregistre l'operation.
    @Column(nullable = false, length = 120)
    private String acteur;
}