import { http } from '@/shared/api/http'

export type CashMovementType = 'DEPOT' | 'RETRAIT'

export type CashMovementResponse = {
  id: string
  typeMouvement: CashMovementType
  montant: number
  dateOperation: string
  motif: string | null
  acteur: string
  createdAt: string
}

export type CashMovementCreateRequest = {
  typeMouvement: CashMovementType
  montant: number
  dateOperation: string
  motif?: string
}

export async function listCashMovements() {
  const { data } = await http.get<CashMovementResponse[]>('/api/mouvements-numeraire')
  return data
}

export async function createCashMovement(payload: CashMovementCreateRequest) {
  const { data } = await http.post<CashMovementResponse>('/api/mouvements-numeraire', payload)
  return data
}
