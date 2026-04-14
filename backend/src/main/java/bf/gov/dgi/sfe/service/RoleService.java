package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.RoleRequest;
import bf.gov.dgi.sfe.dto.RoleResponse;
import bf.gov.dgi.sfe.entity.Role;
import bf.gov.dgi.sfe.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Gestion des roles et permissions applicatives.
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleResponse create(RoleRequest request) {
        Role role = new Role();
        role.setCode(request.code());
        role.setLibelle(request.libelle());
        role = roleRepository.save(role);
        return toResponse(role);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        return toResponse(roleRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Role introuvable")));
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId(), role.getCode(), role.getLibelle());
    }
}
