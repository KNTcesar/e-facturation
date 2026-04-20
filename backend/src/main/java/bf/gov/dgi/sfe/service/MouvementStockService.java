package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.MouvementStockResponse;
import bf.gov.dgi.sfe.entity.MouvementStock;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.enums.TypeMouvementStock;
import bf.gov.dgi.sfe.enums.TypeOrigineMouvementStock;
import bf.gov.dgi.sfe.repository.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
// Journalise les mouvements de stock pour audit metier.
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final AuditService auditService;

    @Transactional
    // Enregistre l'entree initiale de stock pour un bien lors de sa creation.
    public void recordInitialStock(Produit produit, BigDecimal quantite, String acteur) {
        BigDecimal qty = normalizeQuantity(quantite);
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setTypeMouvement(TypeMouvementStock.ENTREE_INITIALE);
        mouvement.setOrigineType(TypeOrigineMouvementStock.PRODUIT);
        mouvement.setQuantite(qty);
        mouvement.setStockAvant(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        mouvement.setStockApres(qty);
        mouvement.setOrigineReference(produit.getReference());
        mouvement.setMotif("Initialisation du stock a la creation du produit");
        mouvement.setActeur(nonBlankActor(acteur));

        MouvementStock saved = mouvementStockRepository.save(mouvement);
        auditService.trace("STOCK_ENTREE_INITIALE", "Produit", produit.getId(), nonBlankActor(acteur), null, null,
                "Entree initiale de stock: " + saved.getQuantite() + " pour produit " + produit.getReference());
    }

    @Transactional
    // Enregistre la sortie de stock liee a une facture de bien.
    public void recordSortieFacture(Produit produit, BigDecimal quantite, BigDecimal stockAvant,
                                    BigDecimal stockApres, String origineReference, String acteur) {
        BigDecimal qty = normalizeQuantity(quantite);
        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setTypeMouvement(TypeMouvementStock.SORTIE_FACTURE);
        mouvement.setOrigineType(TypeOrigineMouvementStock.FACTURE);
        mouvement.setQuantite(qty);
        mouvement.setStockAvant(normalizeQuantity(stockAvant));
        mouvement.setStockApres(normalizeQuantity(stockApres));
        mouvement.setOrigineReference(trimToNull(origineReference));
        mouvement.setMotif("Sortie de stock suite a emission de facture");
        mouvement.setActeur(nonBlankActor(acteur));

        MouvementStock saved = mouvementStockRepository.save(mouvement);
        auditService.trace("STOCK_SORTIE_FACTURE", "Produit", produit.getId(), nonBlankActor(acteur), null, null,
                "Sortie stock facture " + (saved.getOrigineReference() == null ? "N/A" : saved.getOrigineReference())
                        + " - quantite: " + saved.getQuantite());
    }

    @Transactional(readOnly = true)
    // Liste les mouvements de stock les plus recents.
    public List<MouvementStockResponse> list() {
        return mouvementStockRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private MouvementStockResponse toResponse(MouvementStock mouvement) {
        return new MouvementStockResponse(
                mouvement.getId(),
                mouvement.getProduit().getId(),
                mouvement.getProduit().getReference(),
                mouvement.getProduit().getDesignation(),
                mouvement.getTypeMouvement(),
                mouvement.getOrigineType(),
                mouvement.getQuantite(),
                mouvement.getStockAvant(),
                mouvement.getStockApres(),
                mouvement.getOrigineReference(),
                mouvement.getMotif(),
                mouvement.getActeur(),
                mouvement.getCreatedAt()
        );
    }

    private BigDecimal normalizeQuantity(BigDecimal value) {
        return value.setScale(3, RoundingMode.HALF_UP);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String nonBlankActor(String acteur) {
        String normalized = trimToNull(acteur);
        return normalized == null ? "system" : normalized;
    }
}
