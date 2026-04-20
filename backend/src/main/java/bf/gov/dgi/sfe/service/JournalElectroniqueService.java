package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.FactureResponse;
import bf.gov.dgi.sfe.dto.JournalElectroniqueResponse;
import bf.gov.dgi.sfe.dto.RapportReglementaireResponse;
import bf.gov.dgi.sfe.entity.JournalElectronique;
import bf.gov.dgi.sfe.enums.TypeDocumentJournalElectronique;
import bf.gov.dgi.sfe.repository.JournalElectroniqueRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// Service de journal electronique des contenus fiscaux.
public class JournalElectroniqueService {

    private final JournalElectroniqueRepository journalElectroniqueRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    // Enregistre une facture dans le journal electronique avec son contenu complet.
    public void recordFacture(FactureResponse facture) {
        Objects.requireNonNull(facture, "facture is required");
        JournalElectronique entry = new JournalElectronique();
        entry.setTypeDocument(TypeDocumentJournalElectronique.FACTURE);
        entry.setDocumentId(facture.id());
        entry.setReferenceDocument(facture.numero());
        entry.setCodeSecefDgi(facture.codeAuthentification());
        entry.setContenuJson(toJson(facture));
        journalElectroniqueRepository.save(entry);
    }

    @Transactional
    // Enregistre un rapport reglementaire dans le journal electronique.
    public void recordRapport(RapportReglementaireResponse rapport) {
        Objects.requireNonNull(rapport, "rapport is required");
        JournalElectronique entry = new JournalElectronique();
        entry.setTypeDocument(TypeDocumentJournalElectronique.RAPPORT);
        entry.setDocumentId(rapport.id());
        entry.setReferenceDocument("RAPPORT-" + rapport.typeRapport().name() + "-" + rapport.generatedAt());
        entry.setCodeSecefDgi(null);
        entry.setContenuJson(toJson(rapport));
        journalElectroniqueRepository.save(entry);
    }

    @Transactional(readOnly = true)
    // Liste les entrees du journal electronique du plus recent au plus ancien.
    public List<JournalElectroniqueResponse> list() {
        return journalElectroniqueRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    // Recupere une entree du journal electronique par son identifiant.
    public JournalElectroniqueResponse get(UUID id) {
        return toResponse(journalElectroniqueRepository.findById(Objects.requireNonNull(id, "id is required"))
                .orElseThrow(() -> new IllegalArgumentException("Entree journal electronique introuvable")));
    }

    // Serialise un objet metier en JSON pour stockage immuable.
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de serialiser l'entree du journal electronique", e);
        }
    }

    // Convertit une entite journal electronique en DTO de reponse.
    private JournalElectroniqueResponse toResponse(JournalElectronique entry) {
        return new JournalElectroniqueResponse(
                entry.getId(),
                entry.getTypeDocument(),
                entry.getDocumentId(),
                entry.getReferenceDocument(),
                entry.getCodeSecefDgi(),
                entry.getContenuJson(),
                entry.getCreatedAt()
        );
    }
}