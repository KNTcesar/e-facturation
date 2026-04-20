package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ProduitRequest;
import bf.gov.dgi.sfe.dto.ProduitResponse;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.enums.GroupeTaxation;
import bf.gov.dgi.sfe.enums.ModePrixArticle;
import bf.gov.dgi.sfe.enums.TypeArticle;
import bf.gov.dgi.sfe.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
// Gestion metier des produits et services facturables.
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final MouvementStockService mouvementStockService;

    @Transactional
    // Cree un produit en normalisant les valeurs numeriques et en derivant HT/TTC selon le mode de prix.
    public ProduitResponse create(ProduitRequest request) {
        BigDecimal prixUnitaire = request.prixUnitaire().setScale(2, RoundingMode.HALF_UP);
        BigDecimal tauxTva = request.tauxTva().setScale(2, RoundingMode.HALF_UP);
        BigDecimal coefficientTva = BigDecimal.ONE.add(tauxTva.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));

        BigDecimal prixUnitaireHt;
        BigDecimal prixUnitaireTtc;
        if (request.modePrixArticle() == ModePrixArticle.TTC) {
            prixUnitaireTtc = prixUnitaire;
            prixUnitaireHt = prixUnitaireTtc.divide(coefficientTva, 2, RoundingMode.HALF_UP);
        } else {
            prixUnitaireHt = prixUnitaire;
            prixUnitaireTtc = prixUnitaireHt.multiply(coefficientTva).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal quantiteDisponible = normalizeQuantiteByType(request.typeArticle(), request.quantite());

        Produit p = new Produit();
        p.setReference(request.reference());
        p.setDesignation(request.designation());
        p.setTypeArticle(request.typeArticle());
        p.setModePrixArticle(request.modePrixArticle());
        p.setPrixUnitaireHt(prixUnitaireHt);
        p.setPrixUnitaireTtc(prixUnitaireTtc);
        p.setTauxTva(tauxTva);
        p.setGroupeTaxation(request.groupeTaxation() == null ? GroupeTaxation.B : request.groupeTaxation());
        p.setTaxeSpecifiqueUnitaire((request.taxeSpecifiqueUnitaire() == null ? BigDecimal.ZERO : request.taxeSpecifiqueUnitaire()).setScale(2, RoundingMode.HALF_UP));
        p.setUnite(request.unite());
        p.setQuantiteDisponible(quantiteDisponible);
        p = produitRepository.save(p);

        if (p.getTypeArticle() != TypeArticle.LOCSER) {
            mouvementStockService.recordInitialStock(p, quantiteDisponible, resolveActor());
        }

        return new ProduitResponse(
                p.getId(),
                p.getReference(),
                p.getDesignation(),
                p.getTypeArticle(),
                p.getModePrixArticle(),
                p.getPrixUnitaireHt(),
                p.getPrixUnitaireTtc(),
                p.getTauxTva(),
                p.getGroupeTaxation(),
                p.getTaxeSpecifiqueUnitaire(),
                p.getUnite(),
                p.getQuantiteDisponible()
        );
    }

    private BigDecimal normalizeQuantiteByType(TypeArticle typeArticle, BigDecimal quantite) {
        if (typeArticle == TypeArticle.LOCSER) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }
        return quantite.setScale(3, RoundingMode.HALF_UP);
    }

    private String resolveActor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return "system";
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional(readOnly = true)
    // Retourne le catalogue des produits avec toutes les informations de tarification et taxation.
    public List<ProduitResponse> list() {
        return produitRepository.findAll().stream()
            .map(p -> new ProduitResponse(
                p.getId(),
                p.getReference(),
                p.getDesignation(),
                p.getTypeArticle(),
                p.getModePrixArticle(),
                p.getPrixUnitaireHt(),
                p.getPrixUnitaireTtc(),
                p.getTauxTva(),
                p.getGroupeTaxation(),
                p.getTaxeSpecifiqueUnitaire(),
                p.getUnite(),
                p.getQuantiteDisponible()
            ))
                .toList();
    }
}
