package bf.gov.dgi.sfe.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Retour complet du profil entreprise.
public record EntrepriseResponse(
        UUID id,
        String nom,
        String ifu,
        String rccm,
        String regimeFiscal,
        String adresse,
        String paysCode,
        String ville,
        String telephone,
        String email,
        String logoUrl,
        LocalDate dateEffet,
        boolean actif,
        List<EtablissementResponse> etablissements,
        List<CertificatFiscalResponse> certificats
) {
}
