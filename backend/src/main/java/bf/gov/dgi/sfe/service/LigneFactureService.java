package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.LigneFactureResponse;
import bf.gov.dgi.sfe.entity.LigneFacture;
import bf.gov.dgi.sfe.repository.LigneFactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Lecture des lignes de facture pour consultation et detail.
public class LigneFactureService {

    private final LigneFactureRepository ligneFactureRepository;

    @Transactional(readOnly = true)
    public List<LigneFactureResponse> listByFacture(UUID factureId) {
        return ligneFactureRepository.findByFactureId(factureId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LigneFactureResponse get(UUID id) {
        return toResponse(ligneFactureRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Ligne facture introuvable")));
    }

    private LigneFactureResponse toResponse(LigneFacture ligne) {
        return new LigneFactureResponse(
                ligne.getId(),
                ligne.getFacture().getId(),
                ligne.getProduit().getId(),
                ligne.getDescription(),
                ligne.getQuantite(),
                ligne.getPrixUnitaireHt(),
                ligne.getTauxTva(),
                ligne.getMontantHt(),
                ligne.getMontantTva(),
                ligne.getMontantTtc()
        );
    }
}
