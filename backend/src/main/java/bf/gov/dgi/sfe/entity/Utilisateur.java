package bf.gov.dgi.sfe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "utilisateurs")
// Compte applicatif authentifie par JWT.
public class Utilisateur extends BaseAuditableEntity {

    // Identifiant de connexion unique.
    @Column(nullable = false, unique = true)
    private String username;

    // Mot de passe deja hashe avant sauvegarde.
    @Column(nullable = false)
    private String passwordHash;

    // Nom affiche dans l'application et les traces d'audit.
    @Column(nullable = false)
    private String nomComplet;

    // Compte actif ou desactive.
    @Column(nullable = false)
    private boolean actif = true;

    // Roles charges directement pour simplifier la securite.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
