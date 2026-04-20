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
            <v-row dense>
              <v-col cols="12" md="4">
                <v-select
                  v-model="createForm.clientMode"
                  :items="clientModeItems"
                  label="Mode client"
                  variant="outlined"
                />
              </v-col>
              <v-col v-if="createForm.clientMode === 'EXISTANT'" cols="12" md="8">
                <v-select
                  v-model="createForm.clientId"
                  :items="clientItems"
                  item-title="label"
                  item-value="value"
                  label="Client"
                  variant="outlined"
                />
              </v-col>
            </v-row>

            <v-row v-if="createForm.clientMode === 'NOUVEAU'" dense>
              <v-col cols="12" md="4">
                <v-select
                  v-model="quickClientForm.typeClient"
                  :items="clientTypeItems"
                  label="Type client"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="8">
                <v-text-field v-model="quickClientForm.nom" label="Nom client" variant="outlined" />
              </v-col>
              <v-col cols="12" v-if="quickIsAnonymousClient">
                <v-alert type="info" variant="tonal" density="comfortable" class="mb-2">
                  Client comptant / anonyme: aucun IFU ni RCCM n'est attendu.
                </v-alert>
              </v-col>
              <v-col cols="12" md="6" v-if="quickRequiresIfu">
                <v-text-field v-model="quickClientForm.ifu" label="IFU" variant="outlined" :required="quickRequiresIfu" />
              </v-col>
              <v-col cols="12" md="6" v-if="quickRequiresRccm">
                <v-text-field v-model="quickClientForm.rccm" label="RCCM" variant="outlined" :required="quickRequiresRccm" />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field v-model="quickClientForm.adresse" label="Adresse" variant="outlined" />
              </v-col>
              <v-col cols="12" md="3">
                <v-text-field v-model="quickClientForm.telephone" label="Téléphone" variant="outlined" />
              </v-col>
              <v-col cols="12" md="3">
                <v-text-field v-model="quickClientForm.email" label="Email" variant="outlined" />
              </v-col>
            </v-row>

            <v-select
              v-model="createForm.produitId"
              :items="productItems"
              item-title="label"
              item-value="value"
              label="Produit"
              variant="outlined"
            />
            <v-alert
              v-if="selectedProduct"
              :type="selectedProduct.typeArticle === 'LOCSER' ? 'success' : 'info'"
              variant="tonal"
              class="mb-3"
            >
              {{ selectedProduct.typeArticle === 'LOCSER'
                ? 'LOCSER non stockable: aucune sortie de stock lors de la facturation'
                : `Bien stockable (${articleTypeLabel(selectedProduct.typeArticle)}): stock disponible ${selectedProduct.quantite}` }}
            </v-alert>

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

            <v-row dense>
              <v-col cols="12" md="6">
                <v-select
                  v-model="createForm.typeFacture"
                  :items="typeFactureItems"
                  label="Type facture"
                  variant="outlined"
                  hint="Choisir le type DGI: vente, acompte, avoir ou version export"
                  persistent-hint
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-select
                  v-model="createForm.modePrixUnitaire"
                  :items="modePrixItems"
                  label="Mode prix unitaire"
                  variant="outlined"
                />
              </v-col>
            </v-row>

            <v-row dense>
              <v-col cols="12" md="6">
                <v-select
                  v-model="createForm.modePaiement"
                  :items="modePaiementItems"
                  label="Mode de paiement"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="createForm.referencePaiement"
                  label="Référence paiement (optionnel)"
                  variant="outlined"
                />
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
            <v-alert v-if="!requiresMarketInfo" type="info" variant="tonal" density="comfortable" class="mb-3">
              Produit de type bien (LOCBIE/IMPBIE): les informations marché ne sont pas obligatoires.
            </v-alert>

            <template v-if="requiresMarketInfo">
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
            </template>

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
import { createClient, listClients, type ClientResponse } from '@/features/clients/api/clientApi'
import { listProducts, type ProductResponse } from '@/features/products/api/productApi'

const router = useRouter()

const clients = ref<ClientResponse[]>([])
const products = ref<ProductResponse[]>([])
const creating = ref(false)
const error = ref('')

const createForm = reactive({
  clientMode: 'NOUVEAU' as 'NOUVEAU' | 'EXISTANT',
  clientId: '',
  produitId: '',
  quantite: 1,
  serieCode: 'A',
  dateEmission: new Date().toISOString().slice(0, 10),
  typeFacture: 'FV' as 'FV' | 'FT' | 'FA' | 'EV' | 'ET' | 'EA',
  modePrixUnitaire: 'HT' as 'HT' | 'TTC',
  modePaiement: 'ESPECES' as 'VIREMENT' | 'CARTE_BANCAIRE' | 'MOBILE_MONEY' | 'CHEQUE' | 'ESPECES' | 'CREDIT',
  referencePaiement: '',
  referenceMarche: '',
  objetMarche: '',
  dateMarche: new Date().toISOString().slice(0, 10),
  dateDebutExecution: new Date().toISOString().slice(0, 10),
  dateFinExecution: new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString().slice(0, 10),
})

const quickClientForm = reactive({
  typeClient: 'PM' as 'CC' | 'PM' | 'PP' | 'PC',
  nom: '',
  ifu: '',
  rccm: '',
  adresse: '',
  telephone: '',
  email: '',
})

const typeFactureItems = [
  { title: 'FV - Facture de vente', value: 'FV' },
  { title: 'FT - Facture d\'acompte ou d\'avance', value: 'FT' },
  { title: 'FA - Facture d\'avoir', value: 'FA' },
  { title: 'EV - Facture de vente à l\'exportation', value: 'EV' },
  { title: 'ET - Facture d\'acompte à l\'exportation', value: 'ET' },
  { title: 'EA - Facture d\'avoir à l\'exportation', value: 'EA' },
]

const modePrixItems = [
  { title: 'HT', value: 'HT' },
  { title: 'TTC', value: 'TTC' },
]

const modePaiementItems = [
  { title: 'Espèces', value: 'ESPECES' },
  { title: 'Virement', value: 'VIREMENT' },
  { title: 'Carte bancaire', value: 'CARTE_BANCAIRE' },
  { title: 'Mobile money', value: 'MOBILE_MONEY' },
  { title: 'Chèque', value: 'CHEQUE' },
  { title: 'Crédit', value: 'CREDIT' },
]

const clientModeItems = [
  { title: 'Nouveau client (saisie directe)', value: 'NOUVEAU' },
  { title: 'Client existant', value: 'EXISTANT' },
]

const clientTypeItems = [
  { title: 'CC - Client comptant / anonyme', value: 'CC' },
  { title: 'PM - Personne morale', value: 'PM' },
  { title: 'PP - Personne physique', value: 'PP' },
  { title: 'PC - Personne physique commerçant', value: 'PC' },
]

const quickIsAnonymousClient = computed(() => quickClientForm.typeClient === 'CC')
const quickRequiresIfu = computed(() => quickClientForm.typeClient === 'PM' || quickClientForm.typeClient === 'PP' || quickClientForm.typeClient === 'PC')
const quickRequiresRccm = computed(() => quickClientForm.typeClient === 'PM' || quickClientForm.typeClient === 'PC')

const clientItems = computed(() =>
  clients.value.map((c) => ({ value: c.id, label: `${c.nom} (${c.ifu ?? 'IFU N/A'})` })),
)

const productItems = computed(() =>
  products.value.map((p) => ({
    value: p.id,
    label: `${p.reference} - ${p.designation} (${articleTypeLabel(p.typeArticle)})`,
  })),
)

const selectedProduct = computed(() =>
  products.value.find((p) => p.id === createForm.produitId) ?? null,
)

const requiresMarketInfo = computed(() => selectedProduct.value?.typeArticle === 'LOCSER')

function articleTypeLabel(typeArticle: ProductResponse['typeArticle']) {
  switch (typeArticle) {
    case 'LOCBIE':
      return 'Bien local'
    case 'LOCSER':
      return 'Service local'
    case 'IMPBIE':
      return 'Bien importé'
  }
}

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
  const selectedClient = createForm.clientMode === 'EXISTANT'
    ? clients.value.find((c) => c.id === createForm.clientId)
    : {
      nom: quickClientForm.nom.trim(),
      ifu: quickClientForm.ifu.trim() || null,
    }
  const selectedProduct = products.value.find((p) => p.id === createForm.produitId)

  if (!createForm.referenceMarche.trim() && selectedClient && selectedProduct) {
    const year = createForm.dateEmission.slice(0, 4)
    const safeIfu = (selectedClient.ifu ?? selectedClient.nom).replace(/[^a-zA-Z0-9]/g, '')
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
  () => [
    createForm.clientMode,
    createForm.clientId,
    quickClientForm.nom,
    quickClientForm.ifu,
    createForm.produitId,
    createForm.dateEmission,
  ],
  () => syncMarketDefaults(),
)

async function submitCreate() {
  creating.value = true
  error.value = ''
  try {
    if (requiresMarketInfo.value) {
      if (
        !createForm.referenceMarche.trim()
        || !createForm.objetMarche.trim()
        || !createForm.dateMarche
        || !createForm.dateDebutExecution
        || !createForm.dateFinExecution
      ) {
        error.value = 'Les champs marché sont obligatoires pour un produit de type Service local (LOCSER).'
        return
      }
    }

    let resolvedClientId = createForm.clientId
    if (createForm.clientMode === 'NOUVEAU') {
      const nom = quickClientForm.nom.trim()
      const adresse = quickClientForm.adresse.trim()

      if (!nom) {
        error.value = 'Le nom du client est obligatoire.'
        return
      }
      if (!adresse) {
        error.value = 'L\'adresse du client est obligatoire.'
        return
      }
      if (quickRequiresIfu.value && !quickClientForm.ifu.trim()) {
        error.value = 'L\'IFU est obligatoire pour ce type de client.'
        return
      }
      if (quickRequiresRccm.value && !quickClientForm.rccm.trim()) {
        error.value = 'Le RCCM est obligatoire pour ce type de client.'
        return
      }

      const createdClient = await createClient({
        typeClient: quickClientForm.typeClient,
        nom,
        ifu: quickClientForm.ifu.trim() || undefined,
        rccm: quickClientForm.rccm.trim() || undefined,
        adresse,
        telephone: quickClientForm.telephone.trim() || undefined,
        email: quickClientForm.email.trim() || undefined,
      })

      clients.value = [createdClient, ...clients.value]
      createForm.clientId = createdClient.id
      resolvedClientId = createdClient.id
    }

    if (!resolvedClientId) {
      error.value = 'Sélectionne un client existant ou saisis un nouveau client.'
      return
    }

    const selectedProduct = products.value.find((p) => p.id === createForm.produitId)
    if (selectedProduct && selectedProduct.typeArticle !== 'LOCSER' && Number(createForm.quantite) > Number(selectedProduct.quantite)) {
      error.value = `Stock insuffisant: demandé ${createForm.quantite}, disponible ${selectedProduct.quantite}.`
      return
    }

    const round2 = (value: number) => Math.round((value + Number.EPSILON) * 100) / 100
    const ceil2 = (value: number) => Math.ceil((value - Number.EPSILON) * 100) / 100

    let paiementMontant = 0
    if (selectedProduct) {
      const quantite = Number(createForm.quantite)
      if (createForm.modePrixUnitaire === 'TTC') {
        paiementMontant = round2(Number(selectedProduct.prixUnitaireTtc) * quantite)
      } else {
        const montantHt = round2(Number(selectedProduct.prixUnitaireHt) * quantite)
        const montantTaxeSpecifique = round2(Number(selectedProduct.taxeSpecifiqueUnitaire) * quantite)
        const baseTaxableTva = round2(montantHt + montantTaxeSpecifique)
        const montantTva = ceil2((baseTaxableTva * Number(selectedProduct.tauxTva)) / 100)
        paiementMontant = round2(baseTaxableTva + montantTva)
      }
    }

    const created = await createInvoice({
      clientId: resolvedClientId,
      serieCode: createForm.serieCode,
      dateEmission: createForm.dateEmission,
      typeFacture: createForm.typeFacture,
      modePrixUnitaire: createForm.modePrixUnitaire,
      ...(requiresMarketInfo.value
        ? {
          referenceMarche: createForm.referenceMarche.trim(),
          objetMarche: createForm.objetMarche.trim(),
          dateMarche: createForm.dateMarche,
          dateDebutExecution: createForm.dateDebutExecution,
          dateFinExecution: createForm.dateFinExecution,
        }
        : {}),
      paiements: paiementMontant > 0
        ? [
          {
            modePaiement: createForm.modePaiement,
            montant: paiementMontant,
            referencePaiement: createForm.referencePaiement.trim() || undefined,
          },
        ]
        : undefined,
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
