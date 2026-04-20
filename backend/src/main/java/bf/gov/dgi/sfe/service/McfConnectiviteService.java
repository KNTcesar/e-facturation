package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.McfConnectiviteStatutResponse;
import bf.gov.dgi.sfe.entity.McfConfiguration;
import bf.gov.dgi.sfe.entity.McfConnectiviteStatut;
import bf.gov.dgi.sfe.enums.TypeConnexionMcf;
import bf.gov.dgi.sfe.repository.McfConnectiviteStatutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
// Suivi quotidien de connectivite MCF et emission d'alerte > 7 jours.
public class McfConnectiviteService {

    private final McfConfigurationService mcfConfigurationService;
    private final McfProtocoleService mcfProtocoleService;
    private final McfConnectiviteStatutRepository mcfConnectiviteStatutRepository;

    @Value("${app.mcf.alert-threshold-days:7}")
    private int thresholdDays;

    @Transactional(readOnly = true)
    // Retourne le statut actif de connectivite, ou calcule un premier statut si absent.
    public McfConnectiviteStatutResponse getStatut() {
        return mcfConnectiviteStatutRepository.findFirstByActifTrueOrderByUpdatedAtDesc()
                .map(this::toResponse)
                .orElseGet(() -> refreshStatut());
    }

    @Transactional
    // Recalcule le statut de connectivite MCF et active une alerte si le seuil est depasse.
    public McfConnectiviteStatutResponse refreshStatut() {
        McfConfiguration configuration = mcfConfigurationService.resolveActiveConfiguration();

        OffsetDateTime lastConnection;
        boolean connected;
        int days;
        boolean alert;
        String message;

        if (configuration.getTypeConnexion() != TypeConnexionMcf.MACHINE) {
            lastConnection = OffsetDateTime.now();
            connected = true;
            days = 0;
            alert = false;
            message = null;
        } else {
            McfProtocoleService.ConnectivityResult result = mcfProtocoleService.executerCommandeConnectivite(configuration);
            lastConnection = result.dateDerniereConnexionAdministration();
            days = (int) ChronoUnit.DAYS.between(lastConnection.toLocalDate(), LocalDate.now());
            connected = days <= thresholdDays;
            alert = days > thresholdDays;
            message = alert
                    ? "Le MCF n'a pas ete connecte a l'Administration depuis " + days + " jours"
                    : null;
        }

        mcfConnectiviteStatutRepository.findAll().forEach(s -> s.setActif(false));

        McfConnectiviteStatut statut = new McfConnectiviteStatut();
        statut.setMcfConfiguration(configuration);
        statut.setConnecteAdministration(connected);
        statut.setDateDerniereConnexionAdministration(lastConnection);
        statut.setDateDerniereVerification(OffsetDateTime.now());
        statut.setJoursDepuisDerniereConnexion(days);
        statut.setAlerteActive(alert);
        statut.setMessageAlerte(message);
        statut.setActif(true);

        McfConnectiviteStatut saved = mcfConnectiviteStatutRepository.save(statut);
        return toResponse(saved);
    }

    @Scheduled(fixedDelayString = "${app.mcf.daily-check-interval-ms:86400000}")
    @Transactional
    // Lance la verification automatique quotidienne de connectivite MCF.
    public void dailyRefresh() {
        refreshStatut();
    }

    // Convertit l'entite de statut MCF en DTO de reponse pour l'UI.
    private McfConnectiviteStatutResponse toResponse(McfConnectiviteStatut statut) {
        McfConfiguration cfg = statut.getMcfConfiguration();
        return new McfConnectiviteStatutResponse(
                statut.getId(),
                cfg.getId(),
                cfg.getTypeConnexion(),
                cfg.getHost(),
                cfg.getPort(),
                statut.isConnecteAdministration(),
                statut.getDateDerniereConnexionAdministration(),
                statut.getDateDerniereVerification(),
                statut.getJoursDepuisDerniereConnexion(),
                statut.isAlerteActive(),
                statut.getMessageAlerte()
        );
    }
}