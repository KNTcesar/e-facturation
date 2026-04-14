<template>
  <div>
    <!-- Catalogue produit: métriques simples et création de références fiscales. -->
    <PageHeader title="Produits" subtitle="Catalogue fiscal des biens et services" >
      <v-btn color="primary" prepend-icon="mdi-plus-circle-outline" @click="openCreate = true">
        Nouveau produit
      </v-btn>
    </PageHeader>

    <div class="module-grid mb-4">
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ products.length }}</p>
        <p class="kpi-label">References actives</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ averageTva }}%</p>
        <p class="kpi-label">TVA moyenne</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ formatCfa(maxPrice) }}</p>
        <p class="kpi-label">Prix HT max</p>
      </v-card>
    </div>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <table class="data-table">
        <thead>
          <tr>
            <th>Reference</th>
            <th>Designation</th>
            <th>Unite</th>
            <th>TVA</th>
            <th>Prix HT</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in products" :key="row.id">
            <td class="mono">{{ row.reference }}</td>
            <td>{{ row.designation }}</td>
            <td>{{ row.unite }}</td>
            <td>{{ row.tauxTva }}%</td>
            <td>{{ formatCfa(row.prixUnitaireHt) }}</td>
          </tr>
        </tbody>
      </table>
    </v-card>

    <v-dialog v-model="openCreate" max-width="620">
      <v-card rounded="xl">
        <v-card-title>Nouveau produit</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="submitCreate">
            <v-text-field v-model="createForm.reference" label="Reference" variant="outlined" />
            <v-text-field v-model="createForm.designation" label="Designation" variant="outlined" />
            <v-text-field v-model.number="createForm.prixUnitaireHt" label="Prix HT" type="number" variant="outlined" />
            <v-text-field v-model.number="createForm.tauxTva" label="Taux TVA" type="number" variant="outlined" />
            <v-text-field v-model="createForm.unite" label="Unite" variant="outlined" />
            <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>
            <v-btn type="submit" color="primary" :loading="creating">Enregistrer</v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
// Les produits servent de base aux lignes de facture et aux indicateurs catalogue.
import { computed, onMounted, reactive, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { createProduct, listProducts, type ProductResponse } from '@/features/products/api/productApi'

const products = ref<ProductResponse[]>([])
const openCreate = ref(false)
const creating = ref(false)
const error = ref('')

const createForm = reactive({
  reference: '',
  designation: '',
  prixUnitaireHt: 0,
  tauxTva: 18,
  unite: 'PCS',
})

const averageTva = computed(() => {
  if (!products.value.length) {
    return '0.0'
  }
  const total = products.value.reduce((acc, p) => acc + Number(p.tauxTva), 0)
  return (total / products.value.length).toFixed(1)
})

const maxPrice = computed(() => {
  if (!products.value.length) {
    return 0
  }
  return Math.max(...products.value.map((p) => Number(p.prixUnitaireHt)))
})

// Chargement du catalogue produit.
async function load() {
  error.value = ''
  try {
    products.value = await listProducts()
  } catch {
    error.value = 'Impossible de charger les produits.'
  }
}

// Ajoute un produit puis le remet immédiatement dans le tableau.
async function submitCreate() {
  creating.value = true
  error.value = ''
  try {
    const created = await createProduct(createForm)
    products.value = [created, ...products.value]
    openCreate.value = false
  } catch {
    error.value = 'Creation impossible. Verifie les donnees saisies.'
  } finally {
    creating.value = false
  }
}

function formatCfa(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}

onMounted(load)
</script>