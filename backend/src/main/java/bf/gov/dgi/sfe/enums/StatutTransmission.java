package bf.gov.dgi.sfe.enums;

// Etat technique d'un envoi vers la DGI.
public enum StatutTransmission {
    PENDING,
    SENT,
    ACK_ACCEPTED,
    ACK_REJECTED,
    RETRY_SCHEDULED,
    FAILED
}
