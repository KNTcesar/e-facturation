<template>
  <div>
    <!-- Gestion des clients: recherche, métriques et création rapide. -->
    <PageHeader title="Clients" subtitle="Gestion des clients et IFU" >
      <v-btn color="primary" prepend-icon="mdi-account-plus-outline" @click="openCreate = true">
        Nouveau client
      </v-btn>
    </PageHeader>

    <div class="module-grid mb-4">
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ filtered.length }}</p>
        <p class="kpi-label">Clients visibles</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ citiesCount }}</p>
        <p class="kpi-label">Villes couvertes</p>
      </v-card>
      <v-card class="module-card pa-4" rounded="xl" elevation="0">
        <p class="kpi-number">{{ withEmail }}</p>
        <p class="kpi-label">Contacts email</p>
      </v-card>
    </div>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <v-text-field
        v-model="search"
        prepend-inner-icon="mdi-magnify"
        label="Rechercher par nom, IFU ou ville"
        variant="outlined"
        density="comfortable"
        hide-details
        class="mb-3"
      />

      <table class="data-table">
        <thead>
          <tr>
            <th>Client</th>
            <th>IFU</th>
            <th>Ville</th>
            <th>Telephone</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filtered" :key="row.id">
            <td>{{ row.nom }}</td>
            <td class="mono">{{ row.ifu }}</td>
            <td>{{ row.adresse }}</td>
            <td>{{ row.telephone }}</td>
            <td>{{ row.email }}</td>
          </tr>
        </tbody>
      </table>
    </v-card>

    <v-dialog v-model="openCreate" max-width="620">
      <v-card rounded="xl">
        <v-card-title>Nouveau client</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="submitCreate">
            <v-text-field v-model="createForm.nom" label="Nom" variant="outlined" />
            <v-text-field v-model="createForm.ifu" label="IFU" variant="outlined" />
            <v-text-field v-model="createForm.adresse" label="Adresse" variant="outlined" />
            <v-text-field v-model="createForm.telephone" label="Telephone" variant="outlined" />
            <v-text-field v-model="createForm.email" label="Email" variant="outlined" />
            <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>
            <v-btn type="submit" color="primary" :loading="creating">Enregistrer</v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
// La page garde les clients en mémoire et filtre côté client pour rester réactive.
import { computed, onMounted, reactive, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { createClient, listClients, type ClientResponse } from '@/features/clients/api/clientApi'

const search = ref('')
const clients = ref<ClientResponse[]>([])
const openCreate = ref(false)
const creating = ref(false)
const error = ref('')

const createForm = reactive({
  nom: '',
  ifu: '',
  adresse: '',
  telephone: '',
  email: '',
})

const filtered = computed(() => {
  const q = search.value.toLowerCase().trim()
  if (!q) {
    return clients.value
  }
  return clients.value.filter((c) =>
    [c.nom, c.ifu, c.adresse].some((value) => value.toLowerCase().includes(q)),
  )
})

const citiesCount = computed(() => new Set(clients.value.map((c) => c.adresse)).size)
const withEmail = computed(() => clients.value.filter((c) => c.email && c.email.length > 0).length)

// Chargement initial de la liste des clients.
async function load() {
  error.value = ''
  try {
    clients.value = await listClients()
  } catch {
    error.value = 'Impossible de charger les clients.'
  }
}

// Création d'un client puis insertion immédiate en tête de liste.
async function submitCreate() {
  creating.value = true
  error.value = ''
  try {
    const created = await createClient(createForm)
    clients.value = [created, ...clients.value]
    openCreate.value = false
    createForm.nom = ''
    createForm.ifu = ''
    createForm.adresse = ''
    createForm.telephone = ''
    createForm.email = ''
  } catch {
    error.value = 'Creation impossible. Verifie les champs ou les droits.'
  } finally {
    creating.value = false
  }
}

onMounted(load)
</script>