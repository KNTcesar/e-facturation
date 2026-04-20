package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.MouvementNumeraireRequest;
import bf.gov.dgi.sfe.dto.MouvementNumeraireResponse;
import bf.gov.dgi.sfe.entity.MouvementNumeraire;
import bf.gov.dgi.sfe.enums.TypeMouvementNumeraire;
import bf.gov.dgi.sfe.repository.MouvementNumeraireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// Gestion des depots et retraits de numeraires.
public class MouvementNumeraireService {

    private final MouvementNumeraireRepository mouvementNumeraireRepository;
    private final AuditService auditService;

    @Transactional
    // Enregistre un mouvement de caisse et trace l'operation dans l'audit.
    public MouvementNumeraireResponse create(MouvementNumeraireRequest request) {
        MouvementNumeraire mouvement = new MouvementNumeraire();
        mouvement.setTypeMouvement(request.typeMouvement());
        mouvement.setMontant(request.montant());
        mouvement.setDateOperation(request.dateOperation());
        mouvement.setMotif(trimToNull(request.motif()));
        mouvement.setActeur(resolveActor());

        MouvementNumeraire saved = mouvementNumeraireRepository.save(mouvement);

        String action = saved.getTypeMouvement() == TypeMouvementNumeraire.DEPOT
                ? "DEPOT_NUMERAIRE"
                : "RETRAIT_NUMERAIRE";
        auditService.trace(action, "MouvementNumeraire", saved.getId(), saved.getActeur());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    // Liste les mouvements de numeraires du plus recent au plus ancien.
    public List<MouvementNumeraireResponse> list() {
        return mouvementNumeraireRepository.findAllByOrderByDateOperationDescCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    // Convertit un mouvement de numeraires en DTO de reponse.
    private MouvementNumeraireResponse toResponse(MouvementNumeraire m) {
        return new MouvementNumeraireResponse(
                m.getId(),
                m.getTypeMouvement(),
                m.getMontant(),
                m.getDateOperation(),
                m.getMotif(),
                m.getActeur(),
                m.getCreatedAt()
        );
    }

    // Nettoie une valeur texte et retourne null si vide.
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // Retourne l'acteur authentifie ou "system" si absence de contexte.
    private String resolveActor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return "system";
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}