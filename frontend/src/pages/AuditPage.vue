<template>
  <div>
    <!-- Journal d'audit et contrôle d'intégrité de la chaîne. -->
    <PageHeader
      title="Audit"
      subtitle="Historique complet des actions et integrite du chainage"
      icon="mdi-shield-check-outline"
    />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>

    <v-card class="soft-panel pa-4 mb-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-2 flex-wrap">
        <div>
          <h3 class="mb-1">Verification de la chaine d'audit</h3>
          <p class="kpi-label mb-0">Controle de l'integrite cryptographique du journal.</p>
        </div>
        <div class="d-flex align-center ga-2">
          <v-chip :color="verifyColor" variant="tonal" label>
            {{ verifyLabel }}
          </v-chip>
          <v-btn color="primary" variant="tonal" prepend-icon="mdi-shield-check-outline" :loading="verifying" @click="onVerify">
            Verifier
          </v-btn>
        </div>
      </div>

      <div v-if="verification" class="mt-3">
        <div><strong>Message:</strong> {{ verification.message }}</div>
        <div><strong>Total entrees:</strong> {{ verification.totalEntries }}</div>
        <div><strong>Premiere entree invalide:</strong> {{ verification.firstBrokenEntryId ?? '-' }}</div>
      </div>
    </v-card>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-2 flex-wrap mb-3">
        <h3 class="mb-0">Journal d'audit</h3>
        <v-btn color="secondary" variant="tonal" prepend-icon="mdi-refresh" :loading="loading" @click="load">
          Recharger
        </v-btn>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Seq</th>
            <th>Date</th>
            <th>Action</th>
            <th>Entite</th>
            <th>Entite ID</th>
            <th>Acteur</th>
            <th>Hash</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="entry in entries" :key="entry.id">
            <td class="mono">{{ entry.sequenceNumber ?? '-' }}</td>
            <td>{{ formatDate(entry.createdAt) }}</td>
            <td>
              <v-chip size="small" color="info" variant="tonal">{{ entry.action }}</v-chip>
            </td>
            <td>{{ entry.entite }}</td>
            <td class="mono">{{ entry.entiteId }}</td>
            <td>{{ entry.acteur }}</td>
            <td class="mono hash-col" :title="entry.entryHash ?? ''">{{ shortHash(entry.entryHash) }}</td>
          </tr>
          <tr v-if="entries.length === 0">
            <td colspan="7" class="text-center py-4">Aucune entree d'audit disponible.</td>
          </tr>
        </tbody>
      </table>
    </v-card>
  </div>
</template>

<script setup lang="ts">
// L'écran audit charge les traces et lance la vérification du chaînage.
import { computed, onMounted, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import {
  listAuditEntries,
  verifyAuditChain,
  type AuditEntryResponse,
  type AuditChainVerificationResponse,
} from '@/features/audit/api/auditApi'

const entries = ref<AuditEntryResponse[]>([])
const verification = ref<AuditChainVerificationResponse | null>(null)
const loading = ref(false)
const verifying = ref(false)
const error = ref('')

const verifyColor = computed(() => {
  if (!verification.value) return 'grey'
  return verification.value.valid ? 'success' : 'error'
})

const verifyLabel = computed(() => {
  if (!verification.value) return 'Non verifie'
  return verification.value.valid ? 'Chaine valide' : 'Chaine invalide'
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    entries.value = await listAuditEntries()
  } catch {
    error.value = "Impossible de charger le journal d'audit."
  } finally {
    loading.value = false
  }
}

// Vérifie le chaînage cryptographique du journal.
async function onVerify() {
  verifying.value = true
  error.value = ''
  try {
    verification.value = await verifyAuditChain()
  } catch {
    error.value = "Impossible de verifier la chaine d'audit."
  } finally {
    verifying.value = false
  }
}

// Formate la date pour la lecture humaine.
function formatDate(value: string) {
  return new Date(value).toLocaleString('fr-FR')
}

// Raccourcit les hash longs pour le tableau.
function shortHash(value: string | null) {
  if (!value) return '-'
  return value.length > 20 ? `${value.slice(0, 20)}...` : value
}

onMounted(async () => {
  await Promise.all([load(), onVerify()])
})
</script>

<style scoped>
.hash-col {
  max-width: 220px;
}
</style>
