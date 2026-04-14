package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.UtilisateurRequest;
import bf.gov.dgi.sfe.dto.UtilisateurResponse;
import bf.gov.dgi.sfe.entity.Role;
import bf.gov.dgi.sfe.entity.Utilisateur;
import bf.gov.dgi.sfe.repository.RoleRepository;
import bf.gov.dgi.sfe.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Gestion des comptes utilisateurs et de leurs roles.
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UtilisateurResponse create(UtilisateurRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(request.username());
        utilisateur.setNomComplet(request.nomComplet());
        utilisateur.setPasswordHash(passwordEncoder.encode(request.password()));
        utilisateur.setActif(true);
        Set<Role> roles = request.roles().stream()
                .map(code -> roleRepository.findByCode(code)
                        .orElseThrow(() -> new IllegalArgumentException("Role introuvable: " + code)))
                .collect(java.util.stream.Collectors.toSet());
        utilisateur.setRoles(roles);
        utilisateur = utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> list() {
        return utilisateurRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurResponse get(UUID id) {
        return toResponse(utilisateurRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable")));
    }

    @Transactional
    public UtilisateurResponse setActivation(UUID id, boolean actif) {
        Utilisateur utilisateur = utilisateurRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setActif(actif);
        return toResponse(utilisateurRepository.save(utilisateur));
    }

    @Transactional
    public UtilisateurResponse resetPassword(UUID id, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Nouveau mot de passe obligatoire");
        }
        Utilisateur utilisateur = utilisateurRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setPasswordHash(passwordEncoder.encode(newPassword));
        return toResponse(utilisateurRepository.save(utilisateur));
    }

    @Transactional
    public void delete(UUID id) {
        Utilisateur utilisateur = utilisateurRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateurRepository.delete(utilisateur);
    }

    private UtilisateurResponse toResponse(Utilisateur utilisateur) {
        Set<String> roles = utilisateur.getRoles().stream().map(Role::getCode).collect(java.util.stream.Collectors.toSet());
        return new UtilisateurResponse(utilisateur.getId(), utilisateur.getUsername(), utilisateur.getNomComplet(), utilisateur.isActif(), roles);
    }
}
