package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.SecefAckRequest;
import bf.gov.dgi.sfe.dto.SecefDispatchResponse;
import bf.gov.dgi.sfe.dto.TransmissionSecefResponse;
import bf.gov.dgi.sfe.entity.Facture;
import bf.gov.dgi.sfe.entity.TransmissionSecef;
import bf.gov.dgi.sfe.enums.StatutFacture;
import bf.gov.dgi.sfe.enums.StatutTransmission;
import bf.gov.dgi.sfe.repository.FactureRepository;
import bf.gov.dgi.sfe.repository.TransmissionSecefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// Consultation des traces d'envoi vers le SECeF.
public class TransmissionSecefService {

    private final TransmissionSecefRepository transmissionSecefRepository;
    private final FactureRepository factureRepository;
    private final SecefGatewayStub secefGatewayStub;
    private final AuditService auditService;

    @Value("${app.secef.max-retries:3}")
    private int maxRetries;

    @Transactional(readOnly = true)
    // Liste les transmissions fiscales connues.
    public List<TransmissionSecefResponse> list() {
        return transmissionSecefRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    // Retourne une transmission par identifiant.
    public TransmissionSecefResponse get(UUID id) {
        return toResponse(transmissionSecefRepository.findById(Objects.requireNonNull(id, "id is required")).orElseThrow(() -> new IllegalArgumentException("Transmission introuvable")));
    }

    @Transactional
    // Envoie un lot limite de transmissions en attente ou en retry.
    public List<SecefDispatchResponse> dispatchPending() {
        List<TransmissionSecef> pending = transmissionSecefRepository.findTop20ByStatutInOrderByCreatedAtAsc(
                List.of(StatutTransmission.PENDING, StatutTransmission.RETRY_SCHEDULED)
        );
        return pending.stream().map(this::dispatchInternal).toList();
    }

    @Transactional
    // Envoie une transmission specifique.
    public SecefDispatchResponse dispatch(UUID id) {
        TransmissionSecef tx = transmissionSecefRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Transmission introuvable"));
        return dispatchInternal(tx);
    }

    @Transactional
    // Traite l'accuse de reception/rejet d'une transmission SECeF.
    public TransmissionSecefResponse processAck(UUID id, SecefAckRequest request) {
        TransmissionSecef tx = transmissionSecefRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Transmission introuvable"));

        if (tx.getStatut() == StatutTransmission.ACK_ACCEPTED || tx.getStatut() == StatutTransmission.ACK_REJECTED) {
            throw new IllegalArgumentException("ACK deja traite pour cette transmission");
        }

        Facture facture = tx.getFacture();
        tx.setDateAccuse(OffsetDateTime.now());
        tx.setCodeRetour(request.codeRetour());
        tx.setMessageRetour(request.messageRetour());

        if (request.accepted()) {
            tx.setStatut(StatutTransmission.ACK_ACCEPTED);
            facture.setStatut(StatutFacture.ACCEPTEE);
            auditService.trace("SECEF_ACK_ACCEPTED", "Facture", facture.getId(), resolveActor());
        } else {
            tx.setStatut(StatutTransmission.ACK_REJECTED);
            facture.setStatut(StatutFacture.REJETEE);
            auditService.trace("SECEF_ACK_REJECTED", "Facture", facture.getId(), resolveActor());
        }

        factureRepository.save(facture);
        transmissionSecefRepository.save(tx);
        return toResponse(tx);
    }

    // Reprise automatique des transmissions en attente de dispatch.
    @Scheduled(fixedDelayString = "${app.secef.dispatch-interval-ms:60000}")
    @Transactional
    // Tache planifiee de reprise automatique des envois en attente.
    public void autoDispatchPending() {
        dispatchPending();
    }

    // Execute la logique d'envoi et met a jour les statuts facture/transmission.
    private SecefDispatchResponse dispatchInternal(TransmissionSecef tx) {
        if (tx.getStatut() == StatutTransmission.ACK_ACCEPTED || tx.getStatut() == StatutTransmission.ACK_REJECTED) {
            return toDispatchResponse(tx);
        }

        Facture facture = tx.getFacture();
        SecefGatewayStub.SecefGatewayResult result = secefGatewayStub.submit(tx);
        tx.setDateEnvoi(OffsetDateTime.now());
        tx.setCodeRetour(result.codeRetour());
        tx.setMessageRetour(result.messageRetour());

        if (result.success()) {
            tx.setStatut(StatutTransmission.SENT);
            facture.setStatut(StatutFacture.ENVOYEE);
            auditService.trace("SECEF_DISPATCH_SENT", "Facture", facture.getId(), resolveActor());
        } else {
            int nextRetry = tx.getRetryCount() + 1;
            tx.setRetryCount(nextRetry);
            if (nextRetry >= maxRetries) {
                tx.setStatut(StatutTransmission.FAILED);
                facture.setStatut(StatutFacture.REJETEE);
                auditService.trace("SECEF_DISPATCH_FAILED", "Facture", facture.getId(), resolveActor());
            } else {
                tx.setStatut(StatutTransmission.RETRY_SCHEDULED);
                auditService.trace("SECEF_DISPATCH_RETRY", "Facture", facture.getId(), resolveActor());
            }
        }

        factureRepository.save(facture);
        transmissionSecefRepository.save(tx);
        return toDispatchResponse(tx);
    }

    // Convertit une transmission en reponse de dispatch simplifiee.
    private SecefDispatchResponse toDispatchResponse(TransmissionSecef tx) {
        return new SecefDispatchResponse(
                tx.getId(),
                tx.getFacture().getId(),
                tx.getStatut(),
                tx.getCodeRetour(),
                tx.getMessageRetour(),
                tx.getRetryCount()
        );
    }

    // Convertit une transmission en DTO de consultation complet.
    private TransmissionSecefResponse toResponse(TransmissionSecef tx) {
        return new TransmissionSecefResponse(
                tx.getId(),
                tx.getFacture().getId(),
                tx.getFormatPayload(),
                tx.getPayloadHash(),
            tx.getPayloadData(),
                tx.getStatut(),
                tx.getCodeRetour(),
                tx.getMessageRetour(),
                tx.getDateEnvoi(),
                tx.getDateAccuse(),
                tx.getRetryCount()
        );
    }

    // Retourne l'acteur courant ou "system" si aucun utilisateur n'est connecte.
    private String resolveActor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return "system";
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
