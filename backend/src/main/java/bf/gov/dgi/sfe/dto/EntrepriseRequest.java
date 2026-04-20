package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

// Donnees de configuration fiscale de l'entreprise.
public record EntrepriseRequest(
        @NotBlank String nom,
        @NotBlank String ifu,
        @NotBlank String rccm,
        @NotBlank String regimeFiscal,
        @NotBlank String serviceImpotRattachement,
        @NotBlank String adresse,
        @NotBlank String paysCode,
        @NotBlank String ville,
        String telephone,
        String email,
        String logoUrl,
        @NotNull LocalDate dateEffet,
        boolean actif,
        @NotEmpty List<EtablissementRequest> etablissements,
        @NotEmpty List<CertificatFiscalRequest> certificats,
        @NotEmpty List<CompteBancaireEntrepriseRequest> comptesBancaires
) {
}
