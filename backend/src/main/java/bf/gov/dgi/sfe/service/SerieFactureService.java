package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.SerieFactureRequest;
import bf.gov.dgi.sfe.dto.SerieFactureResponse;
import bf.gov.dgi.sfe.entity.SerieFacture;
import bf.gov.dgi.sfe.repository.SerieFactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Gestion des series de numerotation facture.
public class SerieFactureService {

    private final SerieFactureRepository serieFactureRepository;

    @Transactional
    // Cree une serie de numerotation pour l'exercice cible.
    public SerieFactureResponse create(SerieFactureRequest request) {
        SerieFacture serie = new SerieFacture();
        serie.setCode(request.code());
        serie.setExercice(request.exercice() > 0 ? request.exercice() : LocalDate.now().getYear());
        serie.setProchainNumero(request.prochainNumero());
        serie.setActive(request.active());
        serie = serieFactureRepository.save(serie);
        return toResponse(serie);
    }

    @Transactional(readOnly = true)
    // Liste les series de facturation configurees.
    public List<SerieFactureResponse> list() {
        return serieFactureRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    // Recupere une serie de facture par identifiant.
    public SerieFactureResponse get(UUID id) {
        return toResponse(serieFactureRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Serie introuvable")));
    }

    // Convertit une entite serie en DTO de reponse.
    private SerieFactureResponse toResponse(SerieFacture serie) {
        return new SerieFactureResponse(serie.getId(), serie.getCode(), serie.getExercice(), serie.getProchainNumero(), serie.isActive());
    }
}
