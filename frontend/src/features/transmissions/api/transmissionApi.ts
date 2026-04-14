import { http } from '@/shared/api/http'

// Représentation d'une transmission SECeF côté UI.
export type TransmissionResponse = {
  id: string
  factureId: string
  formatPayload: string
  payloadHash: string
  statut: 'PENDING' | 'SENT' | 'ACK_ACCEPTED' | 'ACK_REJECTED' | 'RETRY_SCHEDULED' | 'FAILED'
  codeRetour: string | null
  messageRetour: string | null
  dateEnvoi: string
  dateAccuse: string | null
  retryCount: number
}

// Réponse renvoyée après un dispatch ou un retry.
export type DispatchResponse = {
  transmissionId: string
  factureId: string
  statut: 'PENDING' | 'SENT' | 'ACK_ACCEPTED' | 'ACK_REJECTED' | 'RETRY_SCHEDULED' | 'FAILED'
  codeRetour: string | null
  messageRetour: string | null
  retryCount: number
}

// Corps d'une accusé de réception simulé ou rejoué manuellement.
export type AckRequest = {
  accepted: boolean
  codeRetour: string
  messageRetour: string
}

// Liste des transmissions à suivre dans l'interface.
export async function listTransmissions() {
  const { data } = await http.get<TransmissionResponse[]>('/api/transmissions-secef')
  return data
}

// Lance le dispatch des transmissions en attente.
export async function dispatchPending() {
  const { data } = await http.post<DispatchResponse[]>('/api/transmissions-secef/dispatch/pending')
  return data
}

// Relance une transmission unique.
export async function dispatchOne(id: string) {
  const { data } = await http.post<DispatchResponse>(`/api/transmissions-secef/${id}/dispatch`)
  return data
}

// Envoie un accusé de réception pour une transmission donnée.
export async function ackTransmission(id: string, payload: AckRequest) {
  const { data } = await http.post<TransmissionResponse>(`/api/transmissions-secef/${id}/ack`, payload)
  return data
}