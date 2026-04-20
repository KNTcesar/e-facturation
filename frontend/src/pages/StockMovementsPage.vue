<template>
  <div>
    <PageHeader
      title="Mouvements de stock"
      subtitle="Journal des entrées et sorties de stock"
      icon="mdi-warehouse"
    />

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
            <th>Date</th>
            <th>Produit</th>
            <th>Type</th>
            <th>Origine</th>
            <th>Quantité</th>
            <th>Stock avant</th>
            <th>Stock après</th>
            <th>Référence</th>
            <th>Acteur</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in movements" :key="row.id">
            <td>{{ formatDateTime(row.createdAt) }}</td>
            <td>{{ row.produitReference }} - {{ row.produitDesignation }}</td>
            <td>
              <v-chip size="small" :color="movementColor(row.typeMouvement)" variant="tonal">
                {{ movementLabel(row.typeMouvement) }}
              </v-chip>
            </td>
            <td>{{ row.origineType }}</td>
            <td>{{ row.quantite }}</td>
            <td>{{ row.stockAvant }}</td>
            <td>{{ row.stockApres }}</td>
            <td>{{ row.origineReference ?? '-' }}</td>
            <td>{{ row.acteur }}</td>
          </tr>
          <tr v-if="movements.length === 0">
            <td colspan="9" class="text-medium-emphasis">Aucun mouvement de stock enregistré.</td>
          </tr>
        </tbody>
      </table>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { listStockMovements, type StockMovementResponse } from '@/features/stock/api/stockMovementApi'

const movements = ref<StockMovementResponse[]>([])
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    movements.value = await listStockMovements()
  } catch {
    error.value = 'Impossible de charger les mouvements de stock.'
  } finally {
    loading.value = false
  }
}

function movementLabel(type: StockMovementResponse['typeMouvement']) {
  if (type === 'ENTREE_INITIALE') {
    return 'Entrée initiale'
  }
  return 'Sortie facture'
}

function movementColor(type: StockMovementResponse['typeMouvement']) {
  if (type === 'ENTREE_INITIALE') {
    return 'success'
  }
  return 'warning'
}

function formatDateTime(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('fr-BF')
}

onMounted(load)
</script>
