package bf.gov.dgi.sfe.dto;

import jakarta.validation.constraints.NotBlank;

// Accuse SECeF recu pour finaliser une transmission.
public record SecefAckRequest(
        boolean accepted,
        @NotBlank String codeRetour,
        @NotBlank String messageRetour
) {
}