import { http } from '@/shared/api/http'

// Fiche produit utilisée dans les factures et le catalogue.
export type ProductResponse = {
  id: string
  reference: string
  designation: string
  typeArticle: 'LOCBIE' | 'LOCSER' | 'IMPBIE'
  modePrixArticle: 'HT' | 'TTC'
  prixUnitaireHt: number
  prixUnitaireTtc: number
  tauxTva: number
  groupeTaxation: 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P'
  taxeSpecifiqueUnitaire: number
  unite: string
  quantite: number
}

// Données requises pour créer un produit.
export type ProductCreateRequest = {
  reference: string
  designation: string
  typeArticle: 'LOCBIE' | 'LOCSER' | 'IMPBIE'
  modePrixArticle: 'HT' | 'TTC'
  prixUnitaire: number
  tauxTva: number
  groupeTaxation?: 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P'
  taxeSpecifiqueUnitaire?: number
  unite: string
  quantite: number
}

// Charge le catalogue produit.
export async function listProducts() {
  const { data } = await http.get<ProductResponse[]>('/api/produits')
  return data
}

// Ajoute un produit au catalogue.
export async function createProduct(payload: ProductCreateRequest) {
  const { data } = await http.post<ProductResponse>('/api/produits', payload)
  return data
}