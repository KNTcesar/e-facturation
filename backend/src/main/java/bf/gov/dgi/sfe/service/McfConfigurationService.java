package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.McfConfigurationRequest;
import bf.gov.dgi.sfe.dto.McfConfigurationResponse;
import bf.gov.dgi.sfe.entity.McfConfiguration;
import bf.gov.dgi.sfe.enums.TypeConnexionMcf;
import bf.gov.dgi.sfe.repository.McfConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// Service de gestion de la configuration du port MCF.
public class McfConfigurationService {

    private final McfConfigurationRepository mcfConfigurationRepository;

    @Value("${app.mcf.default-host:127.0.0.1}")
    private String defaultHost;

    @Value("${app.mcf.default-port:9090}")
    private int defaultPort;

    @Transactional
    // Retourne la configuration MCF active; cree la config par defaut si absente.
    public McfConfigurationResponse getActive() {
        return toResponse(resolveActiveConfiguration());
    }

    @Transactional
    // Remplace la configuration active par les nouvelles valeurs fournies.
    public McfConfigurationResponse update(McfConfigurationRequest request) {
        mcfConfigurationRepository.findAll().forEach(cfg -> cfg.setActif(false));

        McfConfiguration active = mcfConfigurationRepository.findFirstByActifTrueOrderByUpdatedAtDesc().orElse(new McfConfiguration());
        active.setTypeConnexion(request.typeConnexion() == null ? TypeConnexionMcf.MACHINE : request.typeConnexion());
        active.setHost(request.host().trim());
        active.setPort(request.port());
        active.setActif(true);
        McfConfiguration saved = mcfConfigurationRepository.save(active);
        return toResponse(saved);
    }

    @Transactional
    // Resout la configuration active en base ou initialise une configuration par defaut.
    public McfConfiguration resolveActiveConfiguration() {
        return mcfConfigurationRepository.findFirstByActifTrueOrderByUpdatedAtDesc()
                .orElseGet(() -> {
                    McfConfiguration cfg = new McfConfiguration();
                    cfg.setTypeConnexion(TypeConnexionMcf.MACHINE);
                    cfg.setHost(defaultHost);
                    cfg.setPort(defaultPort);
                    cfg.setActif(true);
                    return mcfConfigurationRepository.save(cfg);
                });
    }

    // Convertit l'entite de configuration MCF en DTO de reponse.
    private McfConfigurationResponse toResponse(McfConfiguration cfg) {
        return new McfConfigurationResponse(
                cfg.getId(),
            cfg.getTypeConnexion(),
                cfg.getHost(),
                cfg.getPort(),
                cfg.isActif(),
                cfg.getUpdatedAt()
        );
    }
}