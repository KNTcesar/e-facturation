import { http } from '@/shared/api/http'

export type ReportType = 'X' | 'Z' | 'A'
export type ReportNature = 'Z_RAPPORT' | 'X_RAPPORT_QUOTIDIEN' | 'X_RAPPORT_PERIODIQUE' | 'A_RAPPORT'

export type ReportRequest = {
  dateDebut?: string | null
  dateFin?: string | null
}

export type ReportTypeFactureSummary = {
  typeFacture: string
  nombreFactures: number
  totalHt: number
  totalTva: number
  totalTtc: number
}

export type ReportTaxationTypeSummary = {
  typeFacture: string
  groupeTaxation: string
  totalMontantTtc: number
  totalBaseTaxableTva: number
  totalMontantTva: number
}

export type ReportModePaiementSummary = {
  modePaiement: string
  montantTotal: number
  nombreTransactions: number
}

export type ReportArticleSummary = {
  codeArticle: string
  designationArticle: string
  uniteMesure: string
  groupeTaxation: string
  prixUnitaire: number
  tauxTaxe: number
  quantiteVendue: number
  quantiteRetournee: number
  quantiteNette: number
  montantEncaisse: number
}

export type ReportResponse = {
  id: string
  typeRapport: ReportType
  natureRapport: ReportNature
  nomCommercial: string
  ifu: string
  isf: string
  dateDebut: string
  dateFin: string
  generatedAt: string
  generatedBy: string
  nombreFactures: number
  totalHt: number
  totalTva: number
  totalTtc: number
  totalReductionsCommerciales: number
  nombreAutresEnregistrementsReductionVentes: number
  montantAutresEnregistrementsReductionVentes: number
  ventesIncompletes: number
  facturesParType: ReportTypeFactureSummary[]
  montantsParGroupeTaxationEtTypeFacture: ReportTaxationTypeSummary[]
  paiementsParMode: ReportModePaiementSummary[]
  articles: ReportArticleSummary[]
}

function normalizeRequest(payload?: ReportRequest): ReportRequest | undefined {
  if (!payload) return undefined
  return {
    dateDebut: payload.dateDebut || null,
    dateFin: payload.dateFin || null,
  }
}

export async function generateReportX(payload?: ReportRequest) {
  const { data } = await http.post<ReportResponse>('/api/rapports-reglementaires/x', normalizeRequest(payload))
  return data
}

export async function generateReportZ(payload?: ReportRequest) {
  const { data } = await http.post<ReportResponse>('/api/rapports-reglementaires/z', normalizeRequest(payload))
  return data
}

export async function generateReportA(payload?: ReportRequest) {
  const { data } = await http.post<ReportResponse>('/api/rapports-reglementaires/a', normalizeRequest(payload))
  return data
}

export async function listReportsHistory() {
  const { data } = await http.get<ReportResponse[]>('/api/rapports-reglementaires')
  return data
}
