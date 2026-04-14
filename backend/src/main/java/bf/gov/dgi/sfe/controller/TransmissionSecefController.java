package bf.gov.dgi.sfe.controller;

import bf.gov.dgi.sfe.dto.SecefAckRequest;
import bf.gov.dgi.sfe.dto.SecefDispatchResponse;
import bf.gov.dgi.sfe.dto.TransmissionSecefResponse;
import bf.gov.dgi.sfe.service.TransmissionSecefService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transmissions-secef")
@RequiredArgsConstructor
// Expose l'historique des transmissions vers le SECeF.
public class TransmissionSecefController {

    private final TransmissionSecefService transmissionSecefService;

    @GetMapping
    public ResponseEntity<List<TransmissionSecefResponse>> list() {
        return ResponseEntity.ok(transmissionSecefService.list());
    }

    // Detail d'une transmission fiscale.
    @GetMapping("/{id}")
    public ResponseEntity<TransmissionSecefResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(transmissionSecefService.get(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/dispatch/pending")
    public ResponseEntity<List<SecefDispatchResponse>> dispatchPending() {
        return ResponseEntity.ok(transmissionSecefService.dispatchPending());
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/dispatch")
    public ResponseEntity<SecefDispatchResponse> dispatchOne(@PathVariable UUID id) {
        return ResponseEntity.ok(transmissionSecefService.dispatch(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','COMPTABLE')")
    @PostMapping("/{id}/ack")
    public ResponseEntity<TransmissionSecefResponse> processAck(
            @PathVariable UUID id,
            @Valid @RequestBody SecefAckRequest request
    ) {
        return ResponseEntity.ok(transmissionSecefService.processAck(id, request));
    }
}
