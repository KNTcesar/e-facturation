import { http } from '@/shared/api/http'

// Modèle client exposé par l'API.
export type ClientResponse = {
  id: string
  nom: string
  ifu: string
  adresse: string
  telephone: string
  email: string
}

// Payload minimal pour créer un client.
export type ClientCreateRequest = {
  nom: string
  ifu: string
  adresse: string
  telephone: string
  email: string
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