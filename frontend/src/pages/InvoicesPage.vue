<template>
  <div>
    <!-- Liste centrale des factures avec actions de certification et de suivi. -->
    <PageHeader title="Factures" subtitle="Emission, numerotation, certification et QR">
      <v-btn color="primary" prepend-icon="mdi-file-document-plus-outline" to="/factures/creer">
        Emettre facture
      </v-btn>
      <v-btn
        class="ml-2"
        color="secondary"
        variant="outlined"
        prepend-icon="mdi-file-document-edit-outline"
        :disabled="!selected.id"
        @click="openClientFormat = true"
      >
        Version client
      </v-btn>
      <v-btn
        class="ml-2"
        color="success"
        variant="tonal"
        prepend-icon="mdi-draw"
        :disabled="!selected.id || selected.statut === 'ANNULEE'"
        @click="openSign = true"
      >
        Signer facture
      </v-btn>
      <v-btn
        class="ml-2"
        color="info"
        variant="tonal"
        prepend-icon="mdi-timer-outline"
        :disabled="!selected.id || selected.statut === 'ANNULEE'"
        @click="openTimestamp = true"
      >
        Horodater
      </v-btn>
      <v-btn
        class="ml-2"
        color="error"
        variant="tonal"
        prepend-icon="mdi-cancel"
        :disabled="!selected.id || selected.statut === 'ANNULEE'"
        @click="openCancel = true"
      >
        Annuler facture
      </v-btn>
    </PageHeader>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>

    <v-row dense>
      <v-col cols="12" lg="7">
        <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
          <div class="d-flex align-center ga-2 mb-3">
            <v-text-field
              v-model="lookupId"
              label="Rechercher facture par UUID"
              variant="outlined"
              density="compact"
              hide-details
            />
            <v-btn color="secondary" variant="tonal" @click="onLookup">Chercher</v-btn>
          </div>

          <table class="data-table">
            <thead>
              <tr>
                <th>Numero</th>
                <th>Exercice</th>
                <th>Date</th>
                <th>Statut</th>
                <th>TTC</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in invoices" :key="row.id" class="clickable" @click="onSelectInvoice(row)">
                <td class="mono">{{ row.numero }}</td>
                <td>{{ row.exercice }}</td>
                <td>{{ row.dateEmission }}</td>
                <td>
                  <v-chip size="small" :color="invoiceStatusUi(row.statut).color" variant="tonal">
                    {{ invoiceStatusUi(row.statut).label }}
                  </v-chip>
                </td>
                <td>{{ formatCfa(row.totalTtc) }}</td>
              </tr>
            </tbody>
          </table>
        </v-card>
      </v-col>

      <v-col cols="12" lg="5">
        <InvoiceComplianceCard
          :invoice="selected"
          :signature="signature"
          :timestamp="timestamp"
          class="mb-4"
        />

        <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
          <h3 class="mb-2">Payload QR</h3>
          <p class="kpi-label mb-3">Texte lisible pour scan, avec payload technique disponible en copie.</p>

          <v-sheet class="qr-sheet pa-3 mono" rounded="lg" color="grey-lighten-5">
            {{ qrPayloadReadable }}
          </v-sheet>

          <div class="d-flex align-start ga-3 mt-3 qr-block">
            <v-sheet class="qr-preview" rounded="lg" color="white" border>
              <img v-if="qrImageDataUrl" :src="qrImageDataUrl" alt="QR facture" />
              <div v-else class="qr-placeholder">QR indisponible</div>
            </v-sheet>

            <v-btn
              class="mt-1"
              color="secondary"
              prepend-icon="mdi-content-copy"
              variant="tonal"
              @click="copyPayload"
            >
              Copier payload
            </v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar.visible" :color="snackbar.color" timeout="2200">
      {{ snackbar.message }}
    </v-snackbar>

    <v-dialog v-model="openCancel" max-width="560">
      <v-card rounded="xl">
        <v-card-title>Annuler la facture</v-card-title>
        <v-card-text>
          <v-textarea
            v-model="cancelReason"
            label="Motif d'annulation"
            variant="outlined"
            rows="4"
            auto-grow
          />
          <div class="d-flex justify-end mt-3 ga-2">
            <v-btn variant="text" @click="openCancel = false">Fermer</v-btn>
            <v-btn color="error" :loading="cancelling" @click="onCancelInvoice">Confirmer annulation</v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="openSign" max-width="640">
      <v-card rounded="xl">
        <v-card-title>Signer la facture</v-card-title>
        <v-card-text>
          <v-alert type="info" variant="tonal" density="comfortable" class="mb-3">
            Charge le certificat et la signature depuis des fichiers. Le Base64 est rempli automatiquement.
          </v-alert>
          <v-file-input
            label="Fichier certificat (.cer, .crt, .pem, .p12...)"
            variant="outlined"
            prepend-icon="mdi-file-certificate-outline"
            show-size
            @update:model-value="onCertFileSelected"
          />
          <v-file-input
            label="Fichier signature (.sig, .p7s...)"
            variant="outlined"
            prepend-icon="mdi-file-sign"
            show-size
            @update:model-value="onSignatureFileSelected"
          />
          <v-textarea v-model="signForm.certificatBase64" label="Certificat Base64 (option avancée)" variant="outlined" rows="4" auto-grow />
          <v-textarea v-model="signForm.signatureBase64" label="Signature Base64 (option avancée)" variant="outlined" rows="4" auto-grow />
          <v-text-field v-model="signForm.algorithme" label="Algorithme" variant="outlined" />
          <div class="d-flex justify-end mt-3 ga-2">
            <v-btn variant="text" @click="openSign = false">Fermer</v-btn>
            <v-btn color="success" :loading="signing" @click="onSignInvoice">Signer</v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="openTimestamp" max-width="640">
      <v-card rounded="xl">
        <v-card-title>Horodater la facture</v-card-title>
        <v-card-text>
          <v-alert type="info" variant="tonal" density="comfortable" class="mb-3">
            Charge le jeton d'horodatage depuis un fichier TSA. Le Base64 est rempli automatiquement.
          </v-alert>
          <v-file-input
            label="Fichier token horodatage (.tsr, .tst, .p7s...)"
            variant="outlined"
            prepend-icon="mdi-file-clock-outline"
            show-size
            @update:model-value="onTimestampTokenFileSelected"
          />
          <v-textarea v-model="timestampForm.tokenHorodatageBase64" label="Token horodatage Base64 (option avancée)" variant="outlined" rows="4" auto-grow />
          <v-text-field v-model="timestampForm.authoriteTemps" label="Autorité de temps" variant="outlined" />
          <v-text-field v-model="timestampForm.algorithmeHash" label="Algorithme de hash" variant="outlined" />
          <div class="d-flex justify-end mt-3 ga-2">
            <v-btn variant="text" @click="openTimestamp = false">Fermer</v-btn>
            <v-btn color="info" :loading="timestamping" @click="onTimestampInvoice">Horodater</v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="openClientFormat" max-width="860">
      <v-card rounded="xl">
        <v-card-title>Facture format client</v-card-title>
        <v-card-text>
          <v-sheet class="invoice-preview" rounded="lg" border>
            <div class="invoice-header">
              <div>
                <h2 class="invoice-title">Facture fiscale</h2>
                <img
                  v-if="invoiceLogoUrl"
                  :src="invoiceLogoUrl"
                  alt="Logo entreprise"
                  class="invoice-company-logo"
                />
                <div class="invoice-subtitle">Document commercial certifie SFE</div>
              </div>
              <div class="invoice-meta-grid">
                <div class="meta-label">REFERENCE</div>
                <div class="meta-label">EMISSION</div>
                <div class="meta-value">{{ selected.numero }}</div>
                <div class="meta-value">{{ selected.dateEmission }}</div>
                <div class="meta-label">IFU CLIENT</div>
                <div class="meta-label">STATUT DGI</div>
                <div class="meta-value">{{ selected.client?.ifu ?? '-' }}</div>
                <div class="meta-value">{{ selected.validationDgi }}</div>
              </div>
            </div>

            <div class="party-grid">
              <div class="party-block">
                <div class="party-title">Client facture</div>
                <div class="party-line">{{ selected.client?.nom ?? '-' }}</div>
                <div class="party-line">IFU: {{ selected.client?.ifu ?? '-' }}</div>
                <div class="party-line">{{ selected.client?.adresse ?? '-' }}</div>
                <div class="party-line">{{ selected.client?.telephone ?? '-' }}</div>
                <div class="party-line">{{ selected.client?.email ?? '-' }}</div>
              </div>
              <div class="party-block">
                <div class="party-title">Entreprise emettrice</div>
                <div class="party-line">{{ selected.entreprise?.nom ?? '-' }}</div>
                <div class="party-line">IFU: {{ selected.entreprise?.ifu ?? '-' }}</div>
                <div class="party-line">RCCM: {{ selected.entreprise?.rccm ?? '-' }}</div>
                <div class="party-line">{{ selected.entreprise?.adresse ?? '-' }}</div>
                <div class="party-line">{{ selected.entreprise?.telephone ?? '-' }}</div>
                <div class="party-line">{{ selected.entreprise?.email ?? '-' }}</div>
              </div>
            </div>

            <div class="market-block">
              <div><strong>Référence marché:</strong> {{ selected.referenceMarche ?? '-' }}</div>
              <div><strong>Objet marché:</strong> {{ selected.objetMarche ?? '-' }}</div>
              <div><strong>Date marché:</strong> {{ selected.dateMarche ?? '-' }}</div>
              <div><strong>Début exécution:</strong> {{ selected.dateDebutExecution ?? '-' }}</div>
              <div><strong>Fin exécution:</strong> {{ selected.dateFinExecution ?? '-' }}</div>
            </div>

            <table class="invoice-lines">
              <thead>
                <tr>
                  <th>DESCRIPTION</th>
                  <th>UNITE</th>
                  <th>QTE</th>
                  <th>PU HT</th>
                  <th>MONTANT</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="line in selected.lignes" :key="line.id">
                  <td>{{ line.produit.reference }} - {{ line.produit.designation }}</td>
                  <td>{{ line.produit.unite }}</td>
                  <td>{{ line.quantite }}</td>
                  <td>{{ formatCfa(line.prixUnitaireHt) }}</td>
                  <td>{{ formatCfa(line.montantTtc) }}</td>
                </tr>
              </tbody>
            </table>

            <div class="totals-grid">
              <div class="notes-box">
                <div><strong>Code auth:</strong> {{ selected.codeAuthentification }}</div>
                <div><strong>Signature:</strong> {{ signature ? 'OK' : 'NON' }}</div>
                <div><strong>Horodatage:</strong> {{ timestamp ? 'OK' : 'NON' }}</div>
                <div class="client-qr-wrap mt-2">
                  <img v-if="qrImageDataUrl" :src="qrImageDataUrl" alt="QR facture client" />
                  <div v-else class="qr-placeholder">QR indisponible</div>
                </div>
              </div>
              <div class="totals-box">
                <div><span>Sous-total HT</span><strong>{{ formatCfa(selected.totalHt) }}</strong></div>
                <div><span>TVA</span><strong>{{ formatCfa(selected.totalTva) }}</strong></div>
                <div class="total-final"><span>TOTAL TTC</span><strong>{{ formatCfa(selected.totalTtc) }}</strong></div>
              </div>
            </div>

            <div class="invoice-footer-message">
              <div style="font-weight:700;margin-bottom:12px;color:#0a495f;font-size:15px;border-bottom:2px solid #0a6f8a;padding-bottom:8px;">PLATEFORME SFE - E-FACTURATION DGI BURKINA FASO</div>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:16px;">
                <div>
                  <p style="font-weight:600;color:#0a6f8a;margin-bottom:6px;">CONTACT SUPPORT</p>
                  <p style="margin:0 0 4px 0;"><strong>Email:</strong> support@sfe.bf</p>
                  <p style="margin:0 0 4px 0;"><strong>Téléphone:</strong> +226 25 00 00 00</p>
                  <p style="margin:0 0 4px 0;"><strong>Adresse:</strong> Ouagadougou, Burkina Faso</p>
                  <p style="margin:0;"><strong>Horaires:</strong> Lun-Ven 08h-17h (GMT)</p>
                </div>
                <div>
                  <p style="font-weight:600;color:#0a6f8a;margin-bottom:6px;">SERVICES FOURNIS</p>
                  <p style="margin:0 0 4px 0;">• Portail e-facturation conforme DGI</p>
                  <p style="margin:0 0 4px 0;">• Transmission sécurisée des factures</p>
                  <p style="margin:0 0 4px 0;">• Archivage électronique sécurisé</p>
                  <p style="margin:0;">• Audit trail immutable et traçable</p>
                </div>
              </div>
              <div style="background:#f0f7fa;padding:12px;border-radius:6px;margin-bottom:16px;border-left:4px solid #0a6f8a;">
                <p style="font-weight:600;color:#0a495f;margin-bottom:6px;">CONDITIONS D'UTILISATION</p>
                <p style="margin:0 0 4px 0;font-size:12px;">Toute facture émise via cette plateforme est conforme aux normes de la DGI et du SECeF. L'utilisateur s'engage à respecter les conditions d'utilisation et les réglementations fiscales en vigueur au Burkina Faso.</p>
              </div>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:16px;">
                <div>
                  <p style="font-weight:600;color:#0a6f8a;margin-bottom:6px;">SÉCURITÉ & CONFORMITÉ</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">✓ Chiffrement SSL/TLS 256-bit</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">✓ Audit trail avec SHA-256</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">✓ Conforme DGI/SECeF</p>
                  <p style="margin:0;font-size:12px;">✓ Sauvegarde redondante</p>
                </div>
                <div>
                  <p style="font-weight:600;color:#0a6f8a;margin-bottom:6px;">DÉLAIS DE RÉPONSE</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">• Transmission: Immédiate</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">• Accusé réception: 24h</p>
                  <p style="margin:0 0 3px 0;font-size:12px;">• Support technique: 4h</p>
                  <p style="margin:0;font-size:12px;">• Archivage: 10 ans minimum</p>
                </div>
              </div>
              <div style="border-top:2px solid #dbe1ea;padding-top:12px;">
                <p style="font-weight:600;color:#0a6f8a;margin-bottom:8px;">RESPONSABILITÉS DE L'UTILISATEUR</p>
                <p style="margin:0 0 4px 0;font-size:12px;">L'utilisateur est responsable de l'exactitude des données soumises, du respect des délais de transmission et de la gestion sécurisée de ses identifiants.</p>
              </div>
              <div style="border-top:2px solid #dbe1ea;padding-top:12px;margin-top:12px;text-align:center;">
                <p style="font-weight:600;color:#0a495f;font-size:12px;margin:0 0 2px 0;">Version 1.0.0 | 2026</p>
                <p style="font-size:11px;color:#617083;margin:0;">© 2026 Plateforme SFE. Tous droits réservés.</p>
              </div>
            </div>
          </v-sheet>

          <v-expansion-panels class="mt-3" variant="accordion">
            <v-expansion-panel title="Texte brut (copie email)">
              <v-expansion-panel-text>
                <v-textarea
                  :model-value="clientInvoiceText"
                  label="Version texte"
                  variant="outlined"
                  rows="10"
                  auto-grow
                  readonly
                />
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
          <div class="d-flex justify-end mt-3 ga-2">
            <v-btn variant="text" @click="openClientFormat = false">Fermer</v-btn>
            <v-btn color="primary" variant="tonal" prepend-icon="mdi-download" @click="downloadClientInvoiceText">
              Exporter .txt
            </v-btn>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-printer" @click="printClientInvoice">
              Export PDF
            </v-btn>
            <v-btn color="secondary" prepend-icon="mdi-content-copy" @click="copyClientInvoiceText">
              Copier le texte
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

  </div>
</template>

<script setup lang="ts">
// Cette page concentre la lecture, la signature, l'horodatage et l'annulation.
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import QRCode from 'qrcode'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { useEntrepriseStore } from '@/features/entreprises/store/useEntrepriseStore'
import { invoiceStatusUi } from '@/shared/utils/status'
import {
  cancelInvoice,
  getInvoice,
  getInvoiceSignature,
  getInvoiceTimestamp,
  listInvoices,
  signInvoice,
  timestampInvoice,
  type InvoiceResponse,
  type SignatureFactureResponse,
  type HorodatageFactureResponse,
} from '@/features/invoices/api/invoiceApi'
import InvoiceComplianceCard from '@/features/invoices/components/InvoiceComplianceCard.vue'

const entrepriseStore = useEntrepriseStore()
const invoices = ref<InvoiceResponse[]>([])
const selected = ref<InvoiceResponse>({
  id: '',
  numero: '-',
  dateEmission: '-',
  statut: 'CERTIFIEE',
  exercice: new Date().getFullYear(),
  totalHt: 0,
  totalTva: 0,
  totalTtc: 0,
  codeAuthentification: '-',
  qrPayload: '-',
  motifAnnulation: null,
  referenceMarche: null,
  objetMarche: null,
  dateMarche: null,
  dateDebutExecution: null,
  dateFinExecution: null,
  validationDgi: 'EN_ATTENTE',
  entreprise: null,
  client: null,
  lignes: [],
})

const openCancel = ref(false)
const openSign = ref(false)
const openTimestamp = ref(false)
const openClientFormat = ref(false)
const cancelling = ref(false)
const signing = ref(false)
const timestamping = ref(false)
const lookupId = ref('')
const error = ref('')
const qrImageDataUrl = ref('')
const cancelReason = ref('')
const signature = ref<SignatureFactureResponse | null>(null)
const timestamp = ref<HorodatageFactureResponse | null>(null)
const snackbar = reactive({
  visible: false,
  color: 'success',
  message: '',
})

const route = useRoute()

const signForm = reactive({
  certificatBase64: '',
  signatureBase64: '',
  algorithme: 'RSA-SHA256',
})

const timestampForm = reactive({
  tokenHorodatageBase64: '',
  authoriteTemps: 'TSA-BURKINA-FASO',
  algorithmeHash: 'SHA-256',
})

// Payload technique compact conservé pour contrôle et copie brute.
const qrPayloadRaw = computed(() => {
  const current = selected.value
  const backendPayload = current.qrPayload?.trim()
  if (backendPayload) {
    return backendPayload
  }

  // Fallback compact payload when backend QR payload is absent.
  return [
    `NUM=${current.numero ?? ''}`,
    `DATE=${current.dateEmission ?? ''}`,
    `TOTAL_TTC=${current.totalTtc ?? 0}`,
    `AUTH=${current.codeAuthentification ?? ''}`,
    `DGI=${current.validationDgi ?? ''}`,
  ].join('|')
})

// Payload lisible encodé dans le QR pour un résultat de scan plus propre.
const qrPayloadReadable = computed(() => {
  const current = selected.value
  const entrepriseNom = current.entreprise?.nom ?? 'N/A'
  const clientNom = current.client?.nom ?? 'N/A'

  return [
    'FACTURE FISCALE - SFE',
    `Numero: ${current.numero || '-'}`,
    `Date: ${current.dateEmission || '-'}`,
    `Client: ${clientNom}`,
    `Entreprise: ${entrepriseNom}`,
    `Montant TTC: ${formatCfa(current.totalTtc || 0)}`,
    `Code auth: ${current.codeAuthentification || '-'}`,
    `Statut DGI: ${current.validationDgi || '-'}`,
    `Ref marche: ${current.referenceMarche || '-'}`,
  ].join('\n')
})

// L'aperçu client récupère le logo directement depuis la facture sélectionnée.
const invoiceLogoUrl = computed(() => {
  const invoiceLogo = selected.value.entreprise?.logoUrl?.trim()
  if (invoiceLogo) {
    return invoiceLogo
  }
  return entrepriseStore.activeEntreprise?.logoUrl?.trim() ?? ''
})

// Texte lisible pour les clients, les exports email et l'impression.
const clientInvoiceText = computed(() => {
  const selectedInvoice = selected.value
  const signatureStatus = signature.value
    ? `Signee (${signature.value.algorithme})`
    : 'Non signee'
  const timestampStatus = timestamp.value
    ? `Horodatee (${timestamp.value.authoriteTemps})`
    : 'Non horodatee'
  const entreprise = selectedInvoice.entreprise
  const client = selectedInvoice.client
  const lignes = selectedInvoice.lignes ?? []

  const lignesText = lignes.length === 0
    ? '- [Aucune ligne detaillee disponible]'
    : lignes.map((line, index) => (
      `${index + 1}. ${line.produit.reference} - ${line.produit.designation} | `
      + `Unite: ${line.produit.unite} | Qté: ${line.quantite} | `
      + `PU HT: ${formatCfa(line.prixUnitaireHt)} | Montant TTC: ${formatCfa(line.montantTtc)}`
    )).join('\n')

  return [
    'FACTURE CLIENT',
    '',
    'Entreprise emettrice',
    `- Nom: ${entreprise?.nom ?? '[A renseigner]'}`,
    `- IFU: ${entreprise?.ifu ?? '[A renseigner]'}`,
    `- RCCM: ${entreprise?.rccm ?? '[A renseigner]'}`,
    `- Adresse: ${entreprise ? `${entreprise.adresse}, ${entreprise.ville} (${entreprise.paysCode})` : '[A renseigner]'}`,
    `- Telephone: ${entreprise?.telephone ?? '[A renseigner]'}`,
    `- Email: ${entreprise?.email ?? '[A renseigner]'}`,
    `- Logo: ${invoiceLogoUrl.value || '[A renseigner]'}`,
    '',
    'Client',
    `- Nom: ${client?.nom ?? '[A renseigner]'}`,
    `- IFU: ${client?.ifu ?? '[A renseigner]'}`,
    `- Adresse: ${client?.adresse ?? '[A renseigner]'}`,
    `- Telephone: ${client?.telephone ?? '[A renseigner]'}`,
    `- Email: ${client?.email ?? '[A renseigner]'}`,
    '',
    `Numero facture: ${selectedInvoice.numero}`,
    `Date emission: ${selectedInvoice.dateEmission}`,
    `Exercice: ${selectedInvoice.exercice}`,
    `Statut: ${selectedInvoice.statut}`,
    `Validation DGI: ${selectedInvoice.validationDgi}`,
    '',
    'Informations marché',
    `- Référence: ${selectedInvoice.referenceMarche ?? '[A renseigner]'}`,
    `- Objet: ${selectedInvoice.objetMarche ?? '[A renseigner]'}`,
    `- Date marché: ${selectedInvoice.dateMarche ?? '[A renseigner]'}`,
    `- Début exécution: ${selectedInvoice.dateDebutExecution ?? '[A renseigner]'}`,
    `- Fin exécution: ${selectedInvoice.dateFinExecution ?? '[A renseigner]'}`,
    '',
    'Lignes produits',
    lignesText,
    '',
    'Montants',
    `- Total HT: ${formatCfa(selectedInvoice.totalHt)}`,
    `- TVA: ${formatCfa(selectedInvoice.totalTva)}`,
    `- Total TTC: ${formatCfa(selectedInvoice.totalTtc)}`,
    '',
    'Conformite fiscale',
    `- Code authentification: ${selectedInvoice.codeAuthentification}`,
    `- Signature: ${signatureStatus}`,
    `- Horodatage: ${timestampStatus}`,
    '',
    'Objet',
    '- Livraison de prestation/solution selon bon de commande ou marche public.',
    '',
    'Nous vous remercions pour votre confiance.',
    'Votre satisfaction est importante pour nous et nous mettons tout en oeuvre pour vous offrir un service professionnel et fiable.',
    `En cas de question ou de probleme concernant cette facture, veuillez contacter ${entreprise?.email ?? 'le service commercial'}${entreprise?.telephone ? ` / ${entreprise.telephone}` : ''}.`,
    'Merci de nous avoir choisis.',
  ].join('\n')
})

async function loadInvoices() {
  error.value = ''
  try {
    invoices.value = await listInvoices()
    if (invoices.value.length > 0) {
      selected.value = invoices.value[0]
      lookupId.value = invoices.value[0].id
    }
  } catch {
    error.value = 'Impossible de charger la liste des factures.'
  }
}

// Met à jour la facture sélectionnée et recharge ses blocs dérivés.
function onSelectInvoice(row: InvoiceResponse) {
  selected.value = row
  lookupId.value = row.id
}

// Recherche une facture directement par UUID.
async function onLookup() {
  error.value = ''
  try {
    const data = await getInvoice(lookupId.value)
    const exists = invoices.value.find((i) => i.id === data.id)
    if (!exists) {
      invoices.value = [data, ...invoices.value]
    }
    selected.value = data
    await loadSignatureAndTimestamp(data.id)
  } catch {
    error.value = 'Facture introuvable pour cet identifiant.'
  }
}

// Charge la signature et l'horodatage associés à la facture courante.
async function loadSignatureAndTimestamp(id: string) {
  try {
    signature.value = await getInvoiceSignature(id)
  } catch {
    signature.value = null
  }

  try {
    timestamp.value = await getInvoiceTimestamp(id)
  } catch {
    timestamp.value = null
  }
}

// Appose une signature sur la facture sélectionnée.
async function onSignInvoice() {
  if (!selected.value.id) return
  if (!signForm.certificatBase64.trim() || !signForm.signatureBase64.trim()) {
    error.value = 'Charge un certificat et une signature valides avant de continuer.'
    return
  }

  signing.value = true
  error.value = ''
  try {
    signature.value = await signInvoice(selected.value.id, {
      certificatBase64: signForm.certificatBase64.trim(),
      signatureBase64: signForm.signatureBase64.trim(),
      algorithme: signForm.algorithme.trim(),
    })
    openSign.value = false
    snackbar.color = 'success'
    snackbar.message = 'Facture signee avec succes.'
    snackbar.visible = true
  } catch {
    error.value = 'Signature impossible pour cette facture.'
  } finally {
    signing.value = false
  }
}

// Horodate la facture sélectionnée.
async function onTimestampInvoice() {
  if (!selected.value.id) return
  if (!timestampForm.tokenHorodatageBase64.trim()) {
    error.value = "Charge un token d'horodatage valide avant de continuer."
    return
  }

  timestamping.value = true
  error.value = ''
  try {
    timestamp.value = await timestampInvoice(selected.value.id, {
      tokenHorodatageBase64: timestampForm.tokenHorodatageBase64.trim(),
      authoriteTemps: timestampForm.authoriteTemps.trim(),
      algorithmeHash: timestampForm.algorithmeHash.trim(),
    })
    openTimestamp.value = false
    snackbar.color = 'success'
    snackbar.message = 'Facture horodatee avec succes.'
    snackbar.visible = true
  } catch {
    error.value = 'Horodatage impossible pour cette facture.'
  } finally {
    timestamping.value = false
  }
}

// Annule la facture après saisie d'un motif explicite.
async function onCancelInvoice() {
  if (!selected.value.id || !cancelReason.value.trim()) {
    error.value = "Le motif d'annulation est obligatoire."
    return
  }

  cancelling.value = true
  error.value = ''
  try {
    const updated = await cancelInvoice(selected.value.id, cancelReason.value.trim())
    selected.value = updated
    invoices.value = invoices.value.map((row) => (row.id === updated.id ? updated : row))
    openCancel.value = false
    cancelReason.value = ''
    snackbar.color = 'success'
    snackbar.message = 'Facture annulee avec succes.'
    snackbar.visible = true
  } catch {
    error.value = "Annulation impossible pour cette facture."
  } finally {
    cancelling.value = false
  }
}

function formatCfa(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}

// Normalise un champ fichier VFileInput en un seul fichier exploitable.
function normalizeFileInput(payload: File | File[] | null): File | null {
  if (!payload) return null
  return Array.isArray(payload) ? payload[0] ?? null : payload
}

// Convertit un fichier en Base64 pour les champs avancés.
function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      const result = reader.result
      if (typeof result !== 'string') {
        reject(new Error('Lecture fichier impossible'))
        return
      }

      const base64Part = result.includes(',') ? result.split(',')[1] : result
      resolve(base64Part)
    }
    reader.onerror = () => reject(new Error('Lecture fichier impossible'))
    reader.readAsDataURL(file)
  })
}

// Lit un certificat local et le copie dans le formulaire de signature.
async function onCertFileSelected(payload: File | File[] | null) {
  const file = normalizeFileInput(payload)
  if (!file) return

  try {
    signForm.certificatBase64 = await fileToBase64(file)
  } catch {
    error.value = 'Impossible de lire le fichier certificat.'
  }
}

// Lit le fichier de signature et prépare le payload Base64.
async function onSignatureFileSelected(payload: File | File[] | null) {
  const file = normalizeFileInput(payload)
  if (!file) return

  try {
    signForm.signatureBase64 = await fileToBase64(file)
  } catch {
    error.value = 'Impossible de lire le fichier signature.'
  }
}

// Lit le jeton TSA et prépare le payload d'horodatage.
async function onTimestampTokenFileSelected(payload: File | File[] | null) {
  const file = normalizeFileInput(payload)
  if (!file) return

  try {
    timestampForm.tokenHorodatageBase64 = await fileToBase64(file)
  } catch {
    error.value = "Impossible de lire le fichier d'horodatage."
  }
}

// Génère un QR code local pour le payload de la facture.
async function refreshQrImage(payload: string) {
  try {
    qrImageDataUrl.value = await QRCode.toDataURL(payload, {
      width: 260,
      margin: 2,
      errorCorrectionLevel: 'M',
      color: {
        dark: '#000000',
        light: '#ffffff',
      },
    })
  } catch {
    qrImageDataUrl.value = ''
  }
}

// Copie le payload QR dans le presse-papiers.
async function copyPayload() {
  try {
    await navigator.clipboard.writeText(qrPayloadRaw.value)
    snackbar.color = 'success'
    snackbar.message = 'Payload technique copie dans le presse-papiers.'
    snackbar.visible = true
  } catch {
    snackbar.color = 'error'
    snackbar.message = 'Copie impossible sur ce navigateur.'
    snackbar.visible = true
  }
}

// Copie la version texte client dans le presse-papiers.
async function copyClientInvoiceText() {
  try {
    await navigator.clipboard.writeText(clientInvoiceText.value)
    snackbar.color = 'success'
    snackbar.message = 'Version client copiee.'
    snackbar.visible = true
  } catch {
    snackbar.color = 'error'
    snackbar.message = 'Copie impossible sur ce navigateur.'
    snackbar.visible = true
  }
}

// Construit un nom de fichier propre pour l'export client.
function invoiceExportFileName() {
  const numero = selected.value.numero && selected.value.numero !== '-' ? selected.value.numero : 'facture'
  const safeNumero = numero.replace(/[^a-zA-Z0-9_-]/g, '_')
  return `facture_${safeNumero}`
}

// Télécharge la version texte de la facture client.
function downloadClientInvoiceText() {
  const blob = new Blob([clientInvoiceText.value], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = `${invoiceExportFileName()}.txt`
  document.body.appendChild(anchor)
  anchor.click()
  document.body.removeChild(anchor)
  URL.revokeObjectURL(url)

  snackbar.color = 'success'
  snackbar.message = 'Export .txt telecharge.'
  snackbar.visible = true
}

// Prépare une fenêtre d'impression autonome pour l'export PDF.
function printClientInvoice() {
  const previewNode = document.querySelector('.invoice-preview') as HTMLElement | null
  if (!previewNode) {
    snackbar.color = 'error'
    snackbar.message = "Aperçu facture introuvable. Ouvre 'Version client' avant d'imprimer."
    snackbar.visible = true
    return
  }

  const printWindow = window.open('', '_blank', 'width=900,height=1200')
  if (!printWindow) {
    snackbar.color = 'error'
    snackbar.message = 'Popup bloquee. Autorise les popups pour exporter en PDF.'
    snackbar.visible = true
    return
  }

  const styleTags = Array.from(document.querySelectorAll('style'))
    .map((tag) => tag.outerHTML)
    .join('\n')

  const stylesheetLinks = Array.from(document.querySelectorAll('link[rel="stylesheet"]'))
    .map((tag) => tag.outerHTML)
    .join('\n')

  printWindow.document.open()
  printWindow.document.write(`
    <!doctype html>
    <html lang="fr">
      <head>
        <meta charset="utf-8" />
        <title>${invoiceExportFileName()}</title>
        ${stylesheetLinks}
        ${styleTags}
        <style>
          @page {
            size: A4;
            margin: 10mm;
          }

          body {
            margin: 0;
            padding: 0;
            background: #fff;
          }

          .print-root {
            padding: 8mm;
          }

          .print-root .invoice-preview {
            box-shadow: none !important;
          }

          @media print {
            .print-root {
              padding: 0;
            }
          }
        </style>
      </head>
      <body>
        <div class="print-root">
          ${previewNode.outerHTML}
        </div>
      </body>
    </html>
  `)
  printWindow.document.close()

  const waitForImages = Array.from(printWindow.document.images).map((img) => {
    if (img.complete) return Promise.resolve()
    return new Promise<void>((resolve) => {
      img.addEventListener('load', () => resolve(), { once: true })
      img.addEventListener('error', () => resolve(), { once: true })
    })
  })

  Promise.all(waitForImages).finally(() => {
    printWindow.focus()
    printWindow.print()
  })
}

watch(
  qrPayloadReadable,
  (payload) => {
    refreshQrImage(payload)
  },
  { immediate: true },
)

watch(
  () => selected.value.id,
  async (id, previousId) => {
    if (!id) {
      signature.value = null
      timestamp.value = null
      return
    }
    if (id === previousId) return
    await loadSignatureAndTimestamp(id)
  },
)

onMounted(async () => {
  if (!entrepriseStore.isLoaded && !entrepriseStore.loading) {
    await entrepriseStore.loadActive()
  }

  await loadInvoices()
  const selectedId = typeof route.query.selected === 'string' ? route.query.selected : ''
  if (selectedId) {
    lookupId.value = selectedId
    await onLookup()
    return
  }

})
</script>

<style scoped>
.clickable {
  cursor: pointer;
}

.clickable:hover {
  background: rgba(10, 147, 150, 0.05);
}

.qr-sheet {
  white-space: break-spaces;
  line-height: 1.55;
}

.qr-block {
  display: flex;
  flex-wrap: wrap;
}

.qr-preview {
  width: 132px;
  height: 132px;
  display: grid;
  place-items: center;
  overflow: hidden;
}

.qr-preview img {
  width: 124px;
  height: 124px;
}

.qr-placeholder {
  font-size: 0.75rem;
  color: #6b7a90;
}

.client-qr-sheet {
  background: #f8fbff;
}

.invoice-preview {
  background: #ffffff;
  border: 1px solid #d2dbea;
  padding: 14px;
}

.invoice-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.invoice-title {
  color: #0a495f;
  font-size: 1.55rem;
  margin: 0 0 6px 0;
  font-weight: 900;
}

.invoice-company-logo {
  max-width: 120px;
  max-height: 60px;
  object-fit: contain;
  border: none;
  border-radius: 4px;
  padding: 0;
  background: transparent;
  margin-bottom: 4px;
}

.invoice-subtitle {
  color: #6b7a90;
  font-size: 0.86rem;
}

.invoice-meta-grid {
  min-width: 320px;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

.meta-label,
.meta-value {
  border: 1px solid #2b8ba7;
  padding: 6px 8px;
  font-size: 0.82rem;
}

.meta-label {
  background: #0a6f8a;
  color: #fff;
  font-weight: 700;
}

.party-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 10px;
}

.party-title {
  background: #0a6f8a;
  color: #fff;
  padding: 4px 7px;
  font-weight: 700;
  font-size: 0.82rem;
  margin-bottom: 4px;
}

.party-line {
  font-size: 0.83rem;
  margin-bottom: 2px;
}

.market-block {
  background: #f5f9ff;
  border: 1px solid #d2dbea;
  padding: 8px;
  margin-bottom: 10px;
  display: grid;
  gap: 2px;
  font-size: 0.82rem;
}

.invoice-lines {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.82rem;
}

.invoice-lines th,
.invoice-lines td {
  border: 1px solid #d2dbea;
  padding: 6px;
}

.invoice-lines th {
  background: #0a6f8a;
  color: #fff;
}

.totals-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 10px;
}

.notes-box {
  border: 1px solid #d2dbea;
  background: #fbfdff;
  padding: 8px;
  font-size: 0.82rem;
}

.totals-box {
  border: 1px solid #d2dbea;
}

.totals-box > div {
  display: flex;
  justify-content: space-between;
  padding: 6px 8px;
  border-bottom: 1px solid #d2dbea;
  font-size: 0.84rem;
}

.totals-box > div:last-child {
  border-bottom: 0;
}

.total-final {
  background: #eef4ff;
  font-weight: 800;
}

.client-qr-wrap {
  width: 160px;
  height: 160px;
  display: grid;
  place-items: center;
}

.client-qr-wrap img {
  width: 148px;
  height: 148px;
}

@media (max-width: 900px) {
  .invoice-header,
  .party-grid,
  .totals-grid {
    grid-template-columns: 1fr;
    display: grid;
  }

  .invoice-meta-grid {
    min-width: 0;
  }
}
</style>