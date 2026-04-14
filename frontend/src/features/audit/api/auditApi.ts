import { http } from '@/shared/api/http'

// Entrée d'audit telle qu'exposée par le backend.
export type AuditEntryResponse = {
  id: string
  sequenceNumber: number | null
  previousEntryHash: string | null
  entryHash: string | null
  action: string
  entite: string
  entiteId: string
  oldHash: string | null
  newHash: string | null
  details: string | null
  acteur: string
  createdAt: string
}

// Résultat de la vérification de la chaîne d'audit.
export type AuditChainVerificationResponse = {
  valid: boolean
  totalEntries: number
  firstBrokenEntryId: string | null
  message: string
}

// Historique complet du journal d'audit.
export async function listAuditEntries() {
  const { data } = await http.get<AuditEntryResponse[]>('/api/journal-audit')
  return data
}

// Vérifie l'intégrité du chaînage cryptographique.
export async function verifyAuditChain() {
  const { data } = await http.get<AuditChainVerificationResponse>('/api/journal-audit/verify')
  return data
}
