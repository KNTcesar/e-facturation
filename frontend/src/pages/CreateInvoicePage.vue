<template>
  <div>
    <!-- Saisie dédiée à la création d'une facture avec référentiels liés. -->
    <PageHeader
      title="Créer une facture"
      subtitle="Saisie de la facture dans un écran dédié"
    >
      <v-btn color="secondary" variant="tonal" prepend-icon="mdi-arrow-left" to="/factures">
        Retour aux factures
      </v-btn>
    </PageHeader>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">
      {{ error }}
    </v-alert>

    <v-row dense>
      <v-col cols="12" lg="8">
        <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
          <h3 class="mb-2">Nouvelle facture</h3>
          <p class="kpi-label mb-4">Saisie métier séparée du suivi des factures.</p>

          <v-form @submit.prevent="submitCreate">
            <v-select
              v-model="createForm.clientId"
              :items="clientItems"
              item-title="label"
              item-value="value"
              label="Client"
              variant="outlined"
            />

            <v-select
              v-model="createForm.produitId"
              :items="productItems"
              item-title="label"
              item-value="value"
              label="Produit"
              variant="outlined"
            />

            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="createForm.quantite"
                  label="Quantité"
                  type="number"
                  min="1"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field v-model="createForm.serieCode" label="Série" variant="outlined" />
              </v-col>
            </v-row>

            <v-text-field
              v-model="createForm.dateEmission"
              label="Date d'émission"
              type="date"
              variant="outlined"
            />

            <v-divider class="my-2" />
            <h4 class="mb-2">Informations marché</h4>

            <v-text-field
              v-model="createForm.referenceMarche"
              label="Référence marché"
              variant="outlined"
              placeholder="MP-MA-2026-001"
              required
            />

            <v-text-field
              v-model="createForm.objetMarche"
              label="Objet du marché"
              variant="outlined"
              placeholder="Livraison logiciel de gestion des nuisibles"
              required
            />

            <v-row dense>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="createForm.dateMarche"
                  label="Date marché"
                  type="date"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="createForm.dateDebutExecution"
                  label="Début exécution"
                  type="date"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="createForm.dateFinExecution"
                  label="Fin exécution"
                  type="date"
                  variant="outlined"
                  required
                />
              </v-col>
            </v-row>

            <div class="d-flex justify-end ga-2 mt-2">
              <v-btn variant="text" to="/factures">Annuler</v-btn>
              <v-btn type="submit" color="primary" :loading="creating">Créer</v-btn>
            </div>
          </v-form>
        </v-card>
      </v-col>

      <v-col cols="12" lg="4">
        <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
          <h3 class="mb-2">Aide</h3>
          <p class="kpi-label mb-3">La facture créée apparaîtra immédiatement dans la liste de suivi.</p>
          <v-alert type="info" variant="tonal" density="comfortable">
            Pense à vérifier le client, le produit et la série avant validation.
          </v-alert>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
// Cette page assemble clients et produits pour créer une facture cohérente.
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { createInvoice } from '@/features/invoices/api/invoiceApi'
import { listClients, type ClientResponse } from '@/features/clients/api/clientApi'
import { listProducts, type ProductResponse } from '@/features/products/api/productApi'

const router = useRouter()

const clients = ref<ClientResponse[]>([])
const products = ref<ProductResponse[]>([])
const creating = ref(false)
const error = ref('')

const createForm = reactive({
  clientId: '',
  produitId: '',
  quantite: 1,
  serieCode: 'A',
  dateEmission: new Date().toISOString().slice(0, 10),
  referenceMarche: '',
  objetMarche: '',
  dateMarche: new Date().toISOString().slice(0, 10),
  dateDebutExecution: new Date().toISOString().slice(0, 10),
  dateFinExecution: new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString().slice(0, 10),
})

const clientItems = computed(() =>
  clients.value.map((c) => ({ value: c.id, label: `${c.nom} (${c.ifu})` })),
)

const productItems = computed(() =>
  products.value.map((p) => ({ value: p.id, label: `${p.reference} - ${p.designation}` })),
)

async function loadReferences() {
  error.value = ''
  try {
    const [clientData, productData] = await Promise.all([listClients(), listProducts()])
    clients.value = clientData
    products.value = productData
    if (clients.value[0]) createForm.clientId = clients.value[0].id
    if (products.value[0]) createForm.produitId = products.value[0].id
    syncMarketDefaults()
  } catch {
    error.value = 'Impossible de charger les référentiels clients/produits.'
  }
}

// Remplit automatiquement les champs marché à partir du client et du produit choisis.
function syncMarketDefaults() {
  const selectedClient = clients.value.find((c) => c.id === createForm.clientId)
  const selectedProduct = products.value.find((p) => p.id === createForm.produitId)

  if (!createForm.referenceMarche.trim() && selectedClient && selectedProduct) {
    const year = createForm.dateEmission.slice(0, 4)
    const safeIfu = selectedClient.ifu.replace(/[^a-zA-Z0-9]/g, '')
    const safeRef = selectedProduct.reference.replace(/[^a-zA-Z0-9]/g, '')
    createForm.referenceMarche = `MARCHE-${year}-${safeIfu}-${safeRef}`
  }

  if (!createForm.objetMarche.trim() && selectedClient && selectedProduct) {
    createForm.objetMarche = `Fourniture ${selectedProduct.designation} pour ${selectedClient.nom}`
  }

  if (!createForm.dateMarche) createForm.dateMarche = createForm.dateEmission
  if (!createForm.dateDebutExecution) createForm.dateDebutExecution = createForm.dateEmission
  if (!createForm.dateFinExecution) {
    const startDate = new Date(createForm.dateDebutExecution || createForm.dateEmission)
    startDate.setMonth(startDate.getMonth() + 1)
    createForm.dateFinExecution = startDate.toISOString().slice(0, 10)
  }
}

watch(
  () => [createForm.clientId, createForm.produitId, createForm.dateEmission],
  () => syncMarketDefaults(),
)

async function submitCreate() {
  creating.value = true
  error.value = ''
  try {
    if (
      !createForm.referenceMarche.trim()
      || !createForm.objetMarche.trim()
      || !createForm.dateMarche
      || !createForm.dateDebutExecution
      || !createForm.dateFinExecution
    ) {
      error.value = 'Les champs marché sont obligatoires et doivent être renseignés.'
      return
    }

    const created = await createInvoice({
      clientId: createForm.clientId,
      serieCode: createForm.serieCode,
      dateEmission: createForm.dateEmission,
      referenceMarche: createForm.referenceMarche.trim(),
      objetMarche: createForm.objetMarche.trim(),
      dateMarche: createForm.dateMarche,
      dateDebutExecution: createForm.dateDebutExecution,
      dateFinExecution: createForm.dateFinExecution,
      lignes: [
        {
          produitId: createForm.produitId,
          quantite: Number(createForm.quantite),
        },
      ],
    })
    await router.push({ name: 'factures', query: { selected: created.id } })
  } catch {
    error.value = 'Création facture impossible. Vérifie les droits et les données.'
  } finally {
    creating.value = false
  }
}

onMounted(loadReferences)
</script>
