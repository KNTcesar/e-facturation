/**
 * Noyau Fiscal Commun
 * Types for fiscal company profile (Entreprise)
 */

export type CertificatFiscalResponse = {
  id: string
  numeroSerie: string
  autoriteEmission: string
  dateDebutValidite: string
  dateFinValidite: string
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
}

export type EntrepriseRequest = {
  nom: string
  ifu: string
  rccm: string
  regimeFiscal: string
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
  autoriteEmission: string
  dateDebutValidite: string
  dateFinValidite: string
  actif: boolean
}
