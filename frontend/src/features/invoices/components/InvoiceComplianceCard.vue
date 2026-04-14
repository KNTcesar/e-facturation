<template>
  <v-card class="compliance-card" rounded="xl" elevation="0">
    <v-card-title class="d-flex align-center justify-space-between gap-3">
      <div class="d-flex align-center gap-2">
        <v-icon icon="mdi-shield-check-outline" color="primary"></v-icon>
        Statut de conformité S4
      </div>
      <v-chip :color="scoreColor" variant="tonal" size="small" label>
        {{ complianceLabel }}
      </v-chip>
    </v-card-title>

    <v-card-text class="pt-0">
      <v-alert type="info" variant="tonal" density="comfortable" class="mb-3">
        Ordre normalisé: créer la facture, puis la signer, puis l'horodater.
      </v-alert>

      <v-list density="compact" bg-color="transparent" class="mb-3">
        <v-list-item
          title="1. Créer"
          subtitle="Sélectionner le client, le produit et générer la facture"
          :prepend-icon="'mdi-check-circle-outline'"
        />
        <v-list-item
          title="2. Signer"
          subtitle="Appliquer la signature numérique après création"
          :prepend-icon="hasSignature ? 'mdi-check-circle-outline' : 'mdi-circle-outline'"
        />
        <v-list-item
          title="3. Horodater"
          subtitle="Appliquer le jeton de temps après signature"
          :prepend-icon="hasTimestamp ? 'mdi-check-circle-outline' : 'mdi-circle-outline'"
        />
      </v-list>

      <div class="compliance-grid mb-4">
        <div class="compliance-item">
          <div class="label">Facture</div>
          <div class="value mono">{{ invoice.numero }}</div>
        </div>
        <div class="compliance-item">
          <div class="label">Statut</div>
          <div class="value">{{ invoiceStatus }}</div>
        </div>
        <div class="compliance-item">
          <div class="label">Signature</div>
          <v-chip :color="signatureColor" size="small" variant="tonal" label>
            {{ signatureLabel }}
          </v-chip>
        </div>
        <div class="compliance-item">
          <div class="label">Horodatage</div>
          <v-chip :color="timestampColor" size="small" variant="tonal" label>
            {{ timestampLabel }}
          </v-chip>
        </div>
      </div>

      <v-alert v-if="needsAttention" type="warning" variant="tonal" density="comfortable" class="mb-3">
        {{ attentionMessage }}
      </v-alert>

      <v-list density="compact" bg-color="transparent">
        <v-list-item title="Code Auth" :subtitle="invoice.codeAuthentification" />
        <v-list-item title="Exercice" :subtitle="String(invoice.exercice)" />
        <v-list-item title="TTC" :subtitle="formatCurrency(invoice.totalTtc)" />
        <v-list-item v-if="invoice.motifAnnulation" title="Motif annulation" :subtitle="invoice.motifAnnulation" />
        <v-list-item v-if="signature" title="Empreinte certificat" :subtitle="signature.certificatFingerprint" />
        <v-list-item v-if="timestamp" title="Autorité de temps" :subtitle="timestamp.authoriteTemps" />
      </v-list>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type {
  HorodatageFactureResponse,
  InvoiceResponse,
  SignatureFactureResponse,
} from '@/features/invoices/api/invoiceApi'

const props = defineProps<{
  invoice: InvoiceResponse
  signature: SignatureFactureResponse | null
  timestamp: HorodatageFactureResponse | null
}>()

const invoiceStatus = computed(() => props.invoice.statut)
const hasSignature = computed(() => Boolean(props.signature))
const hasTimestamp = computed(() => Boolean(props.timestamp))
const complianceLevel = computed(() => Number(hasSignature.value) + Number(hasTimestamp.value))

const complianceLabel = computed(() => {
  if (complianceLevel.value === 2) return 'Conforme'
  if (complianceLevel.value === 1) return 'Partiel'
  return 'A renforcer'
})

const scoreColor = computed(() => {
  if (complianceLevel.value === 2) return 'success'
  if (complianceLevel.value === 1) return 'warning'
  return 'error'
})

const signatureLabel = computed(() => {
  if (!props.signature) return 'Non signée'
  return props.signature.verifie ? 'Vérifiée' : 'À vérifier'
})

const timestampLabel = computed(() => {
  if (!props.timestamp) return 'Non horodatée'
  return props.timestamp.verifie ? 'Vérifié' : 'À vérifier'
})

const signatureColor = computed(() => {
  if (!props.signature) return 'grey'
  return props.signature.verifie ? 'success' : 'warning'
})

const timestampColor = computed(() => {
  if (!props.timestamp) return 'grey'
  return props.timestamp.verifie ? 'success' : 'warning'
})

const needsAttention = computed(() => !hasSignature.value || !hasTimestamp.value)
const attentionMessage = computed(() => {
  if (!hasSignature.value && !hasTimestamp.value) {
    return 'La facture doit être signée puis horodatée pour être pleinement conforme.'
  }
  if (!hasSignature.value) {
    return 'Signature manquante. La facture doit être signée avant archivage.'
  }
  if (!hasTimestamp.value) {
    return 'Horodatage manquant. La facture doit être horodatée après signature.'
  }
  return ''
})

function formatCurrency(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}
</script>

<style scoped>
.compliance-card {
  border: 1px solid rgba(10, 147, 150, 0.12);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(243, 248, 251, 0.98));
}

.compliance-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.compliance-item {
  padding: 0.75rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(10, 147, 150, 0.08);
}

.label {
  font-size: 0.75rem;
  opacity: 0.65;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 0.35rem;
}

.value {
  font-weight: 700;
}

.mono {
  font-family: 'Courier New', monospace;
}

@media (max-width: 600px) {
  .compliance-grid {
    grid-template-columns: 1fr;
  }
}
</style>
