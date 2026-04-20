import { http } from '@/shared/api/http'

// Modèle client exposé par l'API.
export type ClientResponse = {
  id: string
  typeClient: 'CC' | 'PM' | 'PP' | 'PC'
  nom: string
  ifu: string | null
  rccm: string | null
  adresse: string
  telephone: string | null
  email: string | null
}

// Payload minimal pour créer un client.
export type ClientCreateRequest = {
  typeClient: 'CC' | 'PM' | 'PP' | 'PC'
  nom: string
  ifu?: string
  rccm?: string
  adresse: string
  telephone?: string
  email?: string
}

// Liste des clients disponibles pour la facturation.
export async function listClients() {
  const { data } = await http.get<ClientResponse[]>('/api/clients')
  return data
}

// Création d'un client depuis le formulaire métier.
export async function createClient(payload: ClientCreateRequest) {
  const { data } = await http.post<ClientResponse>('/api/clients', payload)
  return data
}