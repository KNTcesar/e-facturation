package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.RapportReglementaireResponse;
import bf.gov.dgi.sfe.service.RapportReglementaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rapports-reglementaires")
@RequiredArgsConstructor
// Generation et consultation des rapports X, Z et A.
public class RapportReglementaireController {

    private final RapportReglementaireService rapportReglementaireService;

    public record RapportRequest(LocalDate dateDebut, LocalDate dateFin) { }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/x")
    // Genere un X-rapport quotidien ou periodique selon la periode fournie.
    public ResponseEntity<RapportReglementaireResponse> generateX(@RequestBody(required = false) RapportRequest request) {
        return ResponseEntity.ok(rapportReglementaireService.generateX(request == null ? null : request.dateDebut(), request == null ? null : request.dateFin()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/z")
    // Genere un Z-rapport de cloture sur la periode demandee.
    public ResponseEntity<RapportReglementaireResponse> generateZ(@RequestBody(required = false) RapportRequest request) {
        return ResponseEntity.ok(rapportReglementaireService.generateZ(request == null ? null : request.dateDebut(), request == null ? null : request.dateFin()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/a")
    // Genere le A-rapport detaille des articles vendus.
    public ResponseEntity<RapportReglementaireResponse> generateA(@RequestBody(required = false) RapportRequest request) {
        return ResponseEntity.ok(rapportReglementaireService.generateA(request == null ? null : request.dateDebut(), request == null ? null : request.dateFin()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @GetMapping
    // Retourne l'historique des rapports deja generes.
    public ResponseEntity<List<RapportReglementaireResponse>> history() {
        return ResponseEntity.ok(rapportReglementaireService.history());
    }
}
