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
        <p class="kpi-number">{{ locbieCount }}</p>
        <p class="kpi-label">Bien local (LOCBIE)</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ locserCount }}</p>
        <p class="kpi-label">Service local (LOCSER)</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ impbieCount }}</p>
        <p class="kpi-label">Bien importé (IMPBIE)</p>
      </v-card>
    </div>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <table class="data-table">
        <thead>
          <tr>
            <th>Reference</th>
            <th>Designation</th>
            <th>Type</th>
            <th>Mode prix</th>
            <th>Taxation</th>
            <th>Unite</th>
            <th>Stock</th>
            <th>TVA</th>
            <th>Prix HT</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in products" :key="row.id">
            <td class="mono">{{ row.reference }}</td>
            <td>{{ row.designation }}</td>
            <td>{{ row.typeArticle }}</td>
            <td>{{ row.modePrixArticle }}</td>
            <td>{{ row.groupeTaxation }}</td>
            <td>{{ row.unite }}</td>
            <td>{{ row.typeArticle === 'LOCSER' ? '-' : row.quantite }}</td>
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
            <v-select
              v-model="createForm.typeArticle"
              :items="typeArticleItems"
              label="Type article"
              variant="outlined"
            />
            <v-select
              v-model="createForm.modePrixArticle"
              :items="modePrixItems"
              label="Mode prix"
              variant="outlined"
            />
            <v-text-field v-model.number="createForm.prixUnitaire" label="Prix unitaire" type="number" variant="outlined" />
            <v-text-field v-model.number="createForm.tauxTva" label="Taux TVA" type="number" variant="outlined" />
            <v-select
              v-model="createForm.groupeTaxation"
              :items="groupeTaxationItems"
              label="Groupe taxation"
              variant="outlined"
              hint="Code fiscal DGI du produit (A à P)"
              persistent-hint
            />
            <v-text-field
              v-model.number="createForm.taxeSpecifiqueUnitaire"
              label="Taxe spécifique unitaire"
              type="number"
              variant="outlined"
            />
            <v-text-field v-model="createForm.unite" label="Unite" variant="outlined" />
            <v-text-field
              v-if="createForm.typeArticle !== 'LOCSER'"
              v-model.number="createForm.quantite"
              label="Stock initial"
              type="number"
              min="0"
              variant="outlined"
              hint="Applicable uniquement aux biens stockables"
              persistent-hint
            />
            <v-alert v-else type="info" variant="tonal" class="mb-3">
              Les services ne sont pas stockables. Le stock sera fixé à 0 automatiquement.
            </v-alert>
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { createProduct, listProducts, type ProductResponse } from '@/features/products/api/productApi'

const products = ref<ProductResponse[]>([])
const openCreate = ref(false)
const creating = ref(false)
const error = ref('')

const createForm = reactive({
  reference: '',
  designation: '',
  typeArticle: 'LOCBIE' as 'LOCBIE' | 'LOCSER' | 'IMPBIE',
  modePrixArticle: 'HT' as 'HT' | 'TTC',
  prixUnitaire: 0,
  tauxTva: 18,
  groupeTaxation: 'B' as 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P',
  taxeSpecifiqueUnitaire: 0,
  unite: 'PCS',
  quantite: 1,
})

const typeArticleItems = [
  { title: 'LOCBIE - Bien (Local)', value: 'LOCBIE' },
  { title: 'LOCSER - Service (Local)', value: 'LOCSER' },
  { title: 'IMPBIE - Bien (Importation)', value: 'IMPBIE' },
]

const modePrixItems = [
  { title: 'HT', value: 'HT' },
  { title: 'TTC', value: 'TTC' },
]

const groupeTaxationItems = [
  { title: 'A - Exonéré', value: 'A' },
  { title: 'B - TVA taxable 1 (18%)', value: 'B' },
  { title: 'C - TVA taxable 2 (10%)', value: 'C' },
  { title: 'D - Exportation de produits taxables', value: 'D' },
  { title: 'E - TVA régime dérogatoire', value: 'E' },
  { title: 'F - TVA régime dérogatoire (18%)', value: 'F' },
  { title: 'G - TVA régime dérogatoire (10%)', value: 'G' },
  { title: 'H - Régime synthétique', value: 'H' },
  { title: 'I - Consignation d\'emballage', value: 'I' },
  { title: 'J - Dépôts, garantie et caution', value: 'J' },
  { title: 'K - Débours', value: 'K' },
  { title: 'L - TDT - Taxe développement touristique (10%)', value: 'L' },
  { title: 'M - Taxe séjour hôtelier communes (10%)', value: 'M' },
  { title: 'N - PBA - Droits fixes destination/classe', value: 'N' },
  { title: 'O - Réservé', value: 'O' },
  { title: 'P - Réservé', value: 'P' },
]

// PSVB groups with rates - prepared for invoice creation UI
// const groupePsvbItems = [
//   { title: 'A - PSVB 2.0%', value: 'A' },
//   { title: 'B - PSVB 1.0%', value: 'B' },
//   { title: 'C - PSVB 0.2%', value: 'C' },
//   { title: 'D - PSVB exonéré', value: 'D' },
// ]

const locbieCount = computed(() => products.value.filter((p) => p.typeArticle === 'LOCBIE').length)
const locserCount = computed(() => products.value.filter((p) => p.typeArticle === 'LOCSER').length)
const impbieCount = computed(() => products.value.filter((p) => p.typeArticle === 'IMPBIE').length)

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
    const payload = {
      ...createForm,
      quantite: createForm.typeArticle === 'LOCSER' ? 0 : Number(createForm.quantite),
    }
    const created = await createProduct(payload)
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

watch(
  () => createForm.typeArticle,
  (typeArticle) => {
    if (typeArticle === 'LOCSER') {
      createForm.quantite = 0
    } else if (createForm.quantite <= 0) {
      createForm.quantite = 1
    }
  },
)

onMounted(load)
</script>