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

// Entrée du journal électronique (documents FACTURE/RAPPORT).
export type JournalElectroniqueEntryResponse = {
  id: string
  typeDocument: 'FACTURE' | 'RAPPORT'
  documentId: string
  referenceDocument: string
  codeSecefDgi: string | null
  contenuJson: string
  createdAt: string
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

// Liste les documents fiscaux stockés dans le journal électronique.
export async function listJournalElectroniqueEntries() {
  const { data } = await http.get<JournalElectroniqueEntryResponse[]>('/api/journal-electronique')
  return data
}
