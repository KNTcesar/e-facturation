import { http } from '@/shared/api/http'

// Fiche produit utilisée dans les factures et le catalogue.
export type ProductResponse = {
  id: string
  reference: string
  designation: string
  prixUnitaireHt: number
  tauxTva: number
  unite: string
}

// Données requises pour créer un produit.
export type ProductCreateRequest = {
  reference: string
  designation: string
  prixUnitaireHt: number
  tauxTva: number
  unite: string
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