type UiStatus = {
  label: string
  color: 'primary' | 'info' | 'success' | 'warning' | 'error' | 'secondary'
}

// Conversion des statuts backend vers une étiquette lisible et une couleur UI cohérente.
export function invoiceStatusUi(status: string): UiStatus {
  switch (status) {
    case 'CERTIFIEE':
      return { label: 'Certifiee', color: 'secondary' }
    case 'ENVOYEE':
      return { label: 'Envoyee', color: 'info' }
    case 'ACCEPTEE':
      return { label: 'Acceptee', color: 'success' }
    case 'REJETEE':
      return { label: 'Rejetee', color: 'error' }
    default:
      return { label: status, color: 'primary' }
  }
}

// Même logique de mapping pour les statuts de transmission SECeF.
export function transmissionStatusUi(status: string): UiStatus {
  switch (status) {
    case 'PENDING':
      return { label: 'Pending', color: 'warning' }
    case 'SENT':
      return { label: 'Sent', color: 'info' }
    case 'ACK_ACCEPTED':
      return { label: 'Ack accepted', color: 'success' }
    case 'ACK_REJECTED':
      return { label: 'Ack rejected', color: 'error' }
    case 'RETRY_SCHEDULED':
      return { label: 'Retry scheduled', color: 'warning' }
    case 'FAILED':
      return { label: 'Failed', color: 'error' }
    default:
      return { label: status, color: 'primary' }
  }
}