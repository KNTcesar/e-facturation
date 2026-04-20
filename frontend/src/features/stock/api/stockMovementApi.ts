import { http } from '@/shared/api/http'

export type StockMovementType = 'ENTREE_INITIALE' | 'SORTIE_FACTURE'
export type StockMovementOriginType = 'PRODUIT' | 'FACTURE'

export type StockMovementResponse = {
  id: string
  produitId: string
  produitReference: string
  produitDesignation: string
  typeMouvement: StockMovementType
  origineType: StockMovementOriginType
  quantite: number
  stockAvant: number
  stockApres: number
  origineReference: string | null
  motif: string | null
  acteur: string
  createdAt: string
}

export async function listStockMovements() {
  const { data } = await http.get<StockMovementResponse[]>('/api/mouvements-stock')
  return data
}
