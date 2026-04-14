<template>
  <div>
    <!-- Suivi des transmissions SECeF avec retry et accusés de réception. -->
    <PageHeader
      title="Transmissions SECeF"
      subtitle="Dispatch, retry et suivi des ACK/REJECT"
    >
      <v-btn color="primary" prepend-icon="mdi-send-check-outline" :loading="dispatching" @click="onDispatchPending">
        Dispatch pending
      </v-btn>
    </PageHeader>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <table class="data-table">
        <thead>
          <tr>
            <th>Transmission</th>
            <th>Facture</th>
            <th>Statut</th>
            <th>Code</th>
            <th>Date envoi</th>
            <th>Retry</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in transmissions" :key="row.id">
            <td class="mono">{{ row.id }}</td>
            <td class="mono">{{ row.factureId }}</td>
            <td>
              <v-chip size="small" :color="transmissionStatusUi(row.statut).color" variant="tonal">
                {{ transmissionStatusUi(row.statut).label }}
              </v-chip>
            </td>
            <td class="mono">{{ row.codeRetour || '-' }}</td>
            <td>{{ formatDate(row.dateEnvoi) }}</td>
            <td>{{ row.retryCount }}</td>
            <td>
              <v-btn
                size="small"
                color="secondary"
                variant="tonal"
                prepend-icon="mdi-reload"
                @click="onRetry(row.id)"
              >
                Retry
              </v-btn>
              <v-btn
                size="small"
                class="ml-2"
                color="success"
                variant="tonal"
                prepend-icon="mdi-check"
                @click="onAck(row.id, true)"
              >
                ACK+
              </v-btn>
            </td>
          </tr>
        </tbody>
      </table>
    </v-card>
  </div>
</template>

<script setup lang="ts">
// Vue de supervision des envois vers le système de transmission.
import { onMounted, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { transmissionStatusUi } from '@/shared/utils/status'
import {
  ackTransmission,
  dispatchOne,
  dispatchPending,
  listTransmissions,
  type TransmissionResponse,
} from '@/features/transmissions/api/transmissionApi'

const transmissions = ref<TransmissionResponse[]>([])
const error = ref('')
const dispatching = ref(false)

async function load() {
  error.value = ''
  try {
    transmissions.value = await listTransmissions()
  } catch {
    error.value = 'Impossible de charger les transmissions.'
  }
}

async function onDispatchPending() {
  dispatching.value = true
  error.value = ''
  try {
    await dispatchPending()
    await load()
  } catch {
    error.value = 'Dispatch pending en erreur.'
  } finally {
    dispatching.value = false
  }
}

// Réessaie l'envoi d'une transmission unique.
async function onRetry(id: string) {
  error.value = ''
  try {
    await dispatchOne(id)
    await load()
  } catch {
    error.value = 'Retry impossible pour cette transmission.'
  }
}

// Simule ou rejoue un accusé de réception depuis l'interface.
async function onAck(id: string, accepted: boolean) {
  error.value = ''
  try {
    await ackTransmission(id, {
      accepted,
      codeRetour: accepted ? '200' : '422',
      messageRetour: accepted ? 'Facture acceptee depuis l\'interface' : 'Facture rejetee depuis l\'interface',
    })
    await load()
  } catch {
    error.value = 'ACK impossible pour cette transmission.'
  }
}

// Lecture simple des dates de transmission.
function formatDate(value: string) {
  return new Date(value).toLocaleString('fr-FR')
}

onMounted(load)
</script>