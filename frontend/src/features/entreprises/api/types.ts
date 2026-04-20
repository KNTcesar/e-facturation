/**
 * Noyau Fiscal Commun
 * Types for fiscal company profile (Entreprise)
 */

export type CertificatFiscalResponse = {
  id: string
  numeroSerie: string
  numeroIsf: string
  autoriteEmission: string
  dateDebutValidite: string
  dateFinValidite: string
  actif: boolean
}

export type CompteBancaireEntrepriseResponse = {
  id: string
  referenceCompte: string
  banque: string | null
  actif: boolean
}

export type EtablissementResponse = {
  id: string
  code: string
  nom: string
  adresse: string
  ville: string
  principal: boolean
  actif: boolean
}

export type EntrepriseResponse = {
  id: string
  nom: string
  ifu: string
  rccm: string
  regimeFiscal: string
  serviceImpotRattachement: string
  adresse: string
  paysCode: string
  ville: string
  telephone: string
  email: string
  logoUrl: string | null
  dateEffet: string
  actif: boolean
  etablissements: EtablissementResponse[]
  certificats: CertificatFiscalResponse[]
  comptesBancaires: CompteBancaireEntrepriseResponse[]
}

export type EntrepriseRequest = {
  nom: string
  ifu: string
  rccm: string
  regimeFiscal: string
  serviceImpotRattachement: string
  adresse: string
  paysCode: string
  ville: string
  telephone: string
  email: string
  logoUrl?: string | null
  dateEffet: string
  actif: boolean
  etablissements: EtablissementRequest[]
  certificats: CertificatFiscalRequest[]
  comptesBancaires: CompteBancaireEntrepriseRequest[]
}

export type EtablissementRequest = {
  code: string
  nom: string
  adresse: string
  ville: string
  principal: boolean
  actif: boolean
}

export type CertificatFiscalRequest = {
  numeroSerie: string
  numeroIsf: string
  autoriteEmission: string
  dateDebutValidite: string
  dateFinValidite: string
  actif: boolean
}

export type CompteBancaireEntrepriseRequest = {
  referenceCompte: string
  banque?: string | null
  actif: boolean
}
