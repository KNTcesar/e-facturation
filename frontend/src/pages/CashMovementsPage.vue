<template>
  <div>
    <PageHeader
      title="Mouvements numéraires"
      subtitle="Dépôts et retraits de caisse"
      icon="mdi-cash-multiple"
    >
      <v-btn color="primary" prepend-icon="mdi-cash-plus" @click="openCreate = true">
        Nouveau mouvement
      </v-btn>
    </PageHeader>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">
      {{ error }}
    </v-alert>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-3 mb-3">
        <h3 class="mb-0">Historique des mouvements</h3>
        <v-btn size="small" variant="tonal" prepend-icon="mdi-refresh" :loading="loading" @click="load">
          Recharger
        </v-btn>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>Date opération</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Motif</th>
            <th>Acteur</th>
            <th>Créé le</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in movements" :key="row.id">
            <td>{{ row.dateOperation }}</td>
            <td>
              <v-chip size="small" :color="row.typeMouvement === 'DEPOT' ? 'success' : 'warning'" variant="tonal">
                {{ row.typeMouvement }}
              </v-chip>
            </td>
            <td>{{ formatCfa(row.montant) }}</td>
            <td>{{ row.motif ?? '-' }}</td>
            <td>{{ row.acteur }}</td>
            <td>{{ formatDateTime(row.createdAt) }}</td>
          </tr>
          <tr v-if="movements.length === 0">
            <td colspan="6" class="text-medium-emphasis">Aucun mouvement enregistré.</td>
          </tr>
        </tbody>
      </table>
    </v-card>

    <v-dialog v-model="openCreate" max-width="560">
      <v-card rounded="xl">
        <v-card-title>Nouveau mouvement numéraire</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="submitCreate">
            <v-select
              v-model="createForm.typeMouvement"
              :items="movementTypeItems"
              label="Type mouvement"
              variant="outlined"
            />

            <v-text-field
              v-model.number="createForm.montant"
              type="number"
              min="0.01"
              step="0.01"
              label="Montant"
              variant="outlined"
            />

            <v-text-field
              v-model="createForm.dateOperation"
              type="date"
              label="Date opération"
              variant="outlined"
            />

            <v-textarea
              v-model="createForm.motif"
              label="Motif (optionnel)"
              variant="outlined"
              rows="3"
              auto-grow
            />

            <div class="d-flex justify-end ga-2 mt-2">
              <v-btn variant="text" @click="openCreate = false">Annuler</v-btn>
              <v-btn type="submit" color="primary" :loading="creating">Enregistrer</v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import {
  createCashMovement,
  listCashMovements,
  type CashMovementResponse,
} from '@/features/cash/api/cashMovementApi'

const movements = ref<CashMovementResponse[]>([])
const loading = ref(false)
const creating = ref(false)
const openCreate = ref(false)
const error = ref('')

const createForm = reactive({
  typeMouvement: 'DEPOT' as 'DEPOT' | 'RETRAIT',
  montant: 0,
  dateOperation: new Date().toISOString().slice(0, 10),
  motif: '',
})

const movementTypeItems = [
  { title: 'DEPOT', value: 'DEPOT' },
  { title: 'RETRAIT', value: 'RETRAIT' },
]

async function load() {
  loading.value = true
  error.value = ''
  try {
    movements.value = await listCashMovements()
  } catch {
    error.value = 'Impossible de charger les mouvements numéraires.'
  } finally {
    loading.value = false
  }
}

async function submitCreate() {
  creating.value = true
  error.value = ''
  try {
    const created = await createCashMovement({
      typeMouvement: createForm.typeMouvement,
      montant: Number(createForm.montant),
      dateOperation: createForm.dateOperation,
      motif: createForm.motif.trim() || undefined,
    })

    movements.value = [created, ...movements.value]
    openCreate.value = false
    createForm.typeMouvement = 'DEPOT'
    createForm.montant = 0
    createForm.dateOperation = new Date().toISOString().slice(0, 10)
    createForm.motif = ''
  } catch {
    error.value = 'Création du mouvement impossible. Vérifie les données ou tes droits.'
  } finally {
    creating.value = false
  }
}

function formatCfa(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}

function formatDateTime(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('fr-BF')
}

onMounted(load)
</script>
