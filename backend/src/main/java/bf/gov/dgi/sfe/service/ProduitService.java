package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ProduitRequest;
import bf.gov.dgi.sfe.dto.ProduitResponse;
import bf.gov.dgi.sfe.entity.Produit;
import bf.gov.dgi.sfe.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// Gestion metier des produits et services facturables.
public class ProduitService {

    private final ProduitRepository produitRepository;

    @Transactional
    public ProduitResponse create(ProduitRequest request) {
        Produit p = new Produit();
        p.setReference(request.reference());
        p.setDesignation(request.designation());
        p.setPrixUnitaireHt(request.prixUnitaireHt());
        p.setTauxTva(request.tauxTva());
        p.setUnite(request.unite());
        p = produitRepository.save(p);
        return new ProduitResponse(p.getId(), p.getReference(), p.getDesignation(), p.getPrixUnitaireHt(), p.getTauxTva(), p.getUnite());
    }

    @Transactional(readOnly = true)
    public List<ProduitResponse> list() {
        return produitRepository.findAll().stream()
                .map(p -> new ProduitResponse(p.getId(), p.getReference(), p.getDesignation(), p.getPrixUnitaireHt(), p.getTauxTva(), p.getUnite()))
                .toList();
    }
}
