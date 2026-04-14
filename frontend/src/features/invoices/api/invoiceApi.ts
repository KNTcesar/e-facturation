import { http } from '@/shared/api/http'

// Vue détaillée d'une facture telle qu'affichée dans l'application.
export type InvoiceResponse = {
  id: string
  numero: string
  dateEmission: string
  statut: 'BROUILLON' | 'CERTIFIEE' | 'ENVOYEE' | 'ACCEPTEE' | 'REJETEE' | 'ANNULEE'
  exercice: number
  totalHt: number
  totalTva: number
  totalTtc: number
  codeAuthentification: string
  qrPayload: string
  motifAnnulation: string | null
  referenceMarche: string | null
  objetMarche: string | null
  dateMarche: string | null
  dateDebutExecution: string | null
  dateFinExecution: string | null
  validationDgi: 'VALIDEE' | 'REJETEE' | 'EN_ATTENTE'
  entreprise: InvoiceEntrepriseInfo | null
  client: InvoiceClientInfo | null
  lignes: InvoiceLineInfo[]
}

// Informations entreprise embarquées dans une facture.
export type InvoiceEntrepriseInfo = {
  id: string
  nom: string
  ifu: string
  rccm: string
  adresse: string
  ville: string
  paysCode: string
  telephone: string | null
  email: string | null
  logoUrl: string | null
}

// Informations client embarquées dans une facture.
export type InvoiceClientInfo = {
  id: string
  nom: string
  ifu: string
  adresse: string
  telephone: string | null
  email: string | null
}

// Référence produit utilisée dans une ligne de facture.
export type InvoiceProductInfo = {
  id: string
  reference: string
  designation: string
  unite: string
}

// Ligne de facture avec calculs de montants.
export type InvoiceLineInfo = {
  id: string
  description: string
  quantite: number
  prixUnitaireHt: number
  tauxTva: number
  montantHt: number
  montantTva: number
  montantTtc: number
  produit: InvoiceProductInfo
}

// Résultat de la signature d'une facture.
export type SignatureFactureResponse = {
  id: string
  factureId: string
  dataBrute: string
  algorithme: string
  certificatFingerprint: string
  dateSignature: string
  verifie: boolean
}

// Résultat de l'horodatage d'une facture.
export type HorodatageFactureResponse = {
  id: string
  factureId: string
  algorithmeHash: string
  authoriteTemps: string
  dateHorodatage: string
  verifie: boolean
}

// Payload de création d'une facture.
export type CreateInvoiceRequest = {
  clientId: string
  serieCode: string
  dateEmission: string
  referenceMarche: string
  objetMarche: string
  dateMarche: string
  dateDebutExecution: string
  dateFinExecution: string
  lignes: Array<{
    produitId: string
    quantite: number
  }>
}

// Données de signature fournies par l'utilisateur.
export type SignerFactureRequest = {
  certificatBase64: string
  signatureBase64: string
  algorithme: string
}

// Données d'horodatage fournies par l'utilisateur.
export type HorodaterFactureRequest = {
  tokenHorodatageBase64: string
  authoriteTemps: string
  algorithmeHash: string
}

// Création d'une facture avec une seule entrée métier.
export async function createInvoice(payload: CreateInvoiceRequest) {
  const { data } = await http.post<InvoiceResponse>('/api/factures', payload)
  return data
}

// Liste des factures pour le tableau principal.
export async function listInvoices() {
  const { data } = await http.get<InvoiceResponse[]>('/api/factures')
  return data
}

// Récupère une facture par identifiant.
export async function getInvoice(id: string) {
  const { data } = await http.get<InvoiceResponse>(`/api/factures/${id}`)
  return data
}

// Annule une facture avec un motif explicite.
export async function cancelInvoice(id: string, motif: string) {
  const { data } = await http.post<InvoiceResponse>(`/api/factures/${id}/annuler`, { motif })
  return data
}

// Appose une signature métier sur la facture.
export async function signInvoice(id: string, payload: SignerFactureRequest) {
  const { data } = await http.post<SignatureFactureResponse>(`/api/factures/${id}/signer`, payload)
  return data
}

// Charge la signature déjà enregistrée pour la facture.
export async function getInvoiceSignature(id: string) {
  const { data } = await http.get<SignatureFactureResponse>(`/api/factures/${id}/signature`)
  return data
}

// Ajoute un horodatage officiel à la facture.
export async function timestampInvoice(id: string, payload: HorodaterFactureRequest) {
  const { data } = await http.post<HorodatageFactureResponse>(`/api/factures/${id}/horodater`, payload)
  return data
}

// Charge l'horodatage déjà enregistré pour la facture.
export async function getInvoiceTimestamp(id: string) {
  const { data } = await http.get<HorodatageFactureResponse>(`/api/factures/${id}/horodatage`)
  return data
}