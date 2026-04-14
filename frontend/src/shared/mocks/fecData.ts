// Jeux de données de démonstration pour les écrans de cockpit et de suivi.
export type ClientItem = {
  id: string
  nom: string
  ifu: string
  ville: string
  telephone: string
  email: string
}

// Jeu de produits fictifs utilisé dans les écrans hors API.
export type ProductItem = {
  id: string
  reference: string
  designation: string
  unite: string
  tauxTva: number
  prixUnitaireHt: number
}

// Vue simplifiée d'une facture de démonstration.
export type InvoiceItem = {
  id: string
  numero: string
  client: string
  dateEmission: string
  statut: 'CERTIFIEE' | 'ENVOYEE' | 'ACCEPTEE' | 'REJETEE'
  totalTtc: number
  qrPayload: string
}

// Vue simplifiée d'une transmission de démonstration.
export type TransmissionItem = {
  id: string
  numeroFacture: string
  statut: 'PENDING' | 'SENT' | 'ACK_ACCEPTED' | 'ACK_REJECTED' | 'RETRY_SCHEDULED' | 'FAILED'
  codeRetour: string
  dateEnvoi: string
  retryCount: number
}

// Indicateurs globaux affichés sur le dashboard de démonstration.
export const dashboardMetrics = {
  facturesJour: 124,
  ttcJour: 4825600,
  transmissionsEnAttente: 9,
  tauxAcceptance: 94.3,
}

// Clients de démo pour alimenter les maquettes et tests visuels.
export const clients: ClientItem[] = [
  { id: 'c-1', nom: 'SODIBUR SA', ifu: '00012345A', ville: 'Ouagadougou', telephone: '+226 70 10 10 10', email: 'finance@sodibur.bf' },
  { id: 'c-2', nom: 'Pharma Faso', ifu: '00067890B', ville: 'Bobo-Dioulasso', telephone: '+226 70 20 20 20', email: 'compta@pharmafaso.bf' },
  { id: 'c-3', nom: 'Agri Services Plus', ifu: '00111111C', ville: 'Koudougou', telephone: '+226 70 30 30 30', email: 'admin@agriservices.bf' },
  { id: 'c-4', nom: 'Transit Sahel', ifu: '00155555D', ville: 'Ouagadougou', telephone: '+226 70 40 40 40', email: 'support@transitsahel.bf' },
]

// Produits de démonstration pour le catalogue.
export const products: ProductItem[] = [
  { id: 'p-1', reference: 'SVC-MAINT-01', designation: 'Maintenance Systeme Fiscal', unite: 'FORFAIT', tauxTva: 18, prixUnitaireHt: 250000 },
  { id: 'p-2', reference: 'LIC-SFE-AN', designation: 'Licence SFE annuelle', unite: 'AN', tauxTva: 18, prixUnitaireHt: 950000 },
  { id: 'p-3', reference: 'MAT-IMPR-FISC', designation: 'Imprimante fiscale certifiee', unite: 'PCS', tauxTva: 18, prixUnitaireHt: 380000 },
  { id: 'p-4', reference: 'FORM-CPT-02', designation: 'Formation equipe comptable', unite: 'JOUR', tauxTva: 18, prixUnitaireHt: 180000 },
]

// Factures de démonstration pour les écrans de listing.
export const invoices: InvoiceItem[] = [
  {
    id: 'f-1',
    numero: 'A-00000124',
    client: 'SODIBUR SA',
    dateEmission: '2026-04-10',
    statut: 'ACCEPTEE',
    totalTtc: 1180000,
    qrPayload: 'NUM=A-00000124|AUTH=1F67AB2C45DE89AA|TTC=1180000.00',
  },
  {
    id: 'f-2',
    numero: 'A-00000123',
    client: 'Pharma Faso',
    dateEmission: '2026-04-10',
    statut: 'ENVOYEE',
    totalTtc: 448400,
    qrPayload: 'NUM=A-00000123|AUTH=7AA9CD2D1133EF77|TTC=448400.00',
  },
  {
    id: 'f-3',
    numero: 'A-00000122',
    client: 'Transit Sahel',
    dateEmission: '2026-04-09',
    statut: 'REJETEE',
    totalTtc: 212400,
    qrPayload: 'NUM=A-00000122|AUTH=2BA8EF7C3377AB09|TTC=212400.00',
  },
]

// Transmissions de démonstration pour le pipeline SECeF.
export const transmissions: TransmissionItem[] = [
  { id: 't-1', numeroFacture: 'A-00000124', statut: 'ACK_ACCEPTED', codeRetour: '200', dateEnvoi: '2026-04-10T08:45:00Z', retryCount: 0 },
  { id: 't-2', numeroFacture: 'A-00000123', statut: 'SENT', codeRetour: 'QUEUED', dateEnvoi: '2026-04-10T09:12:00Z', retryCount: 0 },
  { id: 't-3', numeroFacture: 'A-00000122', statut: 'ACK_REJECTED', codeRetour: '422', dateEnvoi: '2026-04-09T17:02:00Z', retryCount: 1 },
  { id: 't-4', numeroFacture: 'A-00000121', statut: 'RETRY_SCHEDULED', codeRetour: 'TEMP_UNAVAILABLE', dateEnvoi: '2026-04-10T09:18:00Z', retryCount: 2 },
]