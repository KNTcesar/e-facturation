/**
 * Client API du noyau fiscal commun.
 * Centralise les appels liés à l'entreprise active et à ses mises à jour.
 */

import { http } from '@/shared/api/http'
import type {
  EntrepriseRequest,
  EntrepriseResponse,
} from './types'

// Crée un nouveau profil fiscal, généralement réservé à un profil administrateur.
export async function createEntreprise(
  payload: EntrepriseRequest
): Promise<EntrepriseResponse> {
  const { data } = await http.post<EntrepriseResponse>('/api/entreprises', payload)
  return data
}

// Retourne tous les profils fiscaux disponibles.
export async function listEntreprises(): Promise<EntrepriseResponse[]> {
  const { data } = await http.get<EntrepriseResponse[]>('/api/entreprises')
  return data
}

// Retourne le profil fiscal actif utilisé pour la génération des factures.
export async function getActiveEntreprise(): Promise<EntrepriseResponse> {
  const { data } = await http.get<EntrepriseResponse>('/api/entreprises/active')
  return data
}

// Récupère un profil fiscal précis par identifiant.
export async function getEntreprise(id: string): Promise<EntrepriseResponse> {
  const { data } = await http.get<EntrepriseResponse>(`/api/entreprises/${id}`)
  return data
}

// Met à jour un profil fiscal existant.
export async function updateEntreprise(
  id: string,
  payload: EntrepriseRequest
): Promise<EntrepriseResponse> {
  const { data } = await http.put<EntrepriseResponse>(`/api/entreprises/${id}`, payload)
  return data
}

// Mise à jour ciblée du logo sans renvoyer tout le formulaire.
export async function updateEntrepriseLogo(
  id: string,
  logoUrl: string | null
): Promise<EntrepriseResponse> {
  const { data } = await http.put<EntrepriseResponse>(`/api/entreprises/${id}`, { logoUrl })
  return data
}
