<template>
  <div>
    <!-- Configuration du noyau fiscal et mise à jour du logo actif. -->
    <PageHeader
      title="Noyau Fiscal Commun"
      subtitle="Profil fiscal, établissements et certificats"
      icon="mdi-file-certificate-outline"
    >
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="showCreateForm = !showCreateForm"
      >
        {{ showCreateForm ? 'Masquer le formulaire' : 'Nouveau noyau fiscal' }}
      </v-btn>
    </PageHeader>

    <v-container fluid>
      <v-alert v-if="submitError" type="error" variant="tonal" class="mb-4">
        {{ submitError }}
      </v-alert>

      <v-alert
        v-if="!entrepriseStore.loading && !entrepriseStore.activeEntreprise"
        type="warning"
        variant="tonal"
        class="mb-4"
      >
        Aucun noyau fiscal actif n'est configuré. Renseigne ce formulaire pour activer la facturation.
      </v-alert>

      <v-card
        v-if="showCreateForm || (!entrepriseStore.loading && !entrepriseStore.activeEntreprise)"
        class="soft-panel pa-4 mb-4"
        rounded="xl"
        elevation="0"
      >
        <h3 class="mb-3">Configurer le noyau fiscal</h3>

        <v-form @submit.prevent="onCreateFiscalCore">
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field v-model="form.nom" label="Nom entreprise" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.ifu" label="IFU" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.rccm" label="RCCM" variant="outlined" required />
            </v-col>
          </v-row>

          <v-row dense>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.regimeFiscal" label="Régime fiscal" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.paysCode" label="Pays (code)" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.ville" label="Ville" variant="outlined" required />
            </v-col>
          </v-row>

          <v-text-field v-model="form.adresse" label="Adresse" variant="outlined" required />

          <v-row dense>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.telephone" label="Téléphone" variant="outlined" />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.email" label="Email" variant="outlined" />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.dateEffet" label="Date d'effet" type="date" variant="outlined" required />
            </v-col>
          </v-row>

          <v-row dense>
            <v-col cols="12" md="6">
              <v-file-input
                label="Logo entreprise (PNG/JPG/SVG)"
                variant="outlined"
                accept="image/png,image/jpeg,image/svg+xml"
                prepend-icon="mdi-image"
                show-size
                @update:model-value="onLogoFileSelected"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.logoUrl"
                label="Logo URL (optionnel)"
                variant="outlined"
                hint="Utilise une URL publique ou importe un fichier"
                persistent-hint
              />
            </v-col>
          </v-row>

          <v-divider class="my-3" />
          <h4 class="mb-2">Établissement principal</h4>
          <v-row dense>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.etablissement.code" label="Code" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.etablissement.nom" label="Nom" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.etablissement.ville" label="Ville" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field v-model="form.etablissement.adresse" label="Adresse" variant="outlined" required />
            </v-col>
          </v-row>

          <v-divider class="my-3" />
          <h4 class="mb-2">Certificat fiscal actif</h4>
          <v-row dense>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.certificat.numeroSerie" label="N° série" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field v-model="form.certificat.autoriteEmission" label="Autorité émission" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="2">
              <v-text-field v-model="form.certificat.dateDebutValidite" label="Début validité" type="date" variant="outlined" required />
            </v-col>
            <v-col cols="12" md="2">
              <v-text-field v-model="form.certificat.dateFinValidite" label="Fin validité" type="date" variant="outlined" required />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-2">
            <v-btn type="submit" color="primary" :loading="submitting">
              Enregistrer le noyau fiscal
            </v-btn>
          </div>
        </v-form>
      </v-card>

      <v-card
        v-if="entrepriseStore.activeEntreprise && !showCreateForm"
        class="soft-panel pa-4 mb-4"
        rounded="xl"
        elevation="0"
      >
        <h3 class="mb-3">Modifier le logo de {{ entrepriseStore.activeEntreprise.nom }}</h3>
        <p class="kpi-label mb-3">Mettez à jour le logo de votre entreprise pour les factures.</p>
        
        <v-row dense>
          <v-col cols="12" md="6">
            <v-file-input
              label="Nouveau logo (PNG/JPG/SVG)"
              variant="outlined"
              accept="image/png,image/jpeg,image/svg+xml"
              prepend-icon="mdi-image"
              show-size
              @update:model-value="onLogoFileSelectedForUpdate"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="logoUpdateForm.logoUrl"
              label="Logo URL (optionnel)"
              variant="outlined"
              hint="Utilise une URL publique ou importe un fichier"
              persistent-hint
            />
          </v-col>
        </v-row>

        <div class="d-flex justify-end mt-2 ga-2">
          <v-btn variant="text" @click="logoUpdateForm.logoUrl = ''">Effacer</v-btn>
          <v-btn color="primary" :loading="updatingLogo" @click="onUpdateEntrepriseLogo">
            Mettre à jour le logo
          </v-btn>
        </div>

        <v-alert v-if="logoUpdateError" type="error" variant="tonal" class="mt-3">
          {{ logoUpdateError }}
        </v-alert>
      </v-card>

      <EntrepriseDisplay v-if="entrepriseStore.activeEntreprise || entrepriseStore.loading" />
    </v-container>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import EntrepriseDisplay from '@/features/entreprises/components/EntrepriseDisplay.vue'
import { createEntreprise, updateEntrepriseLogo } from '@/features/entreprises/api/entrepriseApi'
import { useEntrepriseStore } from '@/features/entreprises/store/useEntrepriseStore'

const entrepriseStore = useEntrepriseStore()
const submitting = ref(false)
const submitError = ref('')
const showCreateForm = ref(false)

// État dédié à la mise à jour du logo de l'entreprise active.
const updatingLogo = ref(false)
const logoUpdateError = ref('')
const logoUpdateForm = reactive({
  logoUrl: '',
})

const form = reactive({
  nom: '',
  ifu: '',
  rccm: '',
  regimeFiscal: '',
  adresse: '',
  paysCode: 'BF',
  ville: '',
  telephone: '',
  email: '',
  logoUrl: '',
  dateEffet: new Date().toISOString().slice(0, 10),
  etablissement: {
    code: 'HQ',
    nom: 'Siège',
    adresse: '',
    ville: '',
  },
  certificat: {
    numeroSerie: '',
    autoriteEmission: 'DGI',
    dateDebutValidite: new Date().toISOString().slice(0, 10),
    dateFinValidite: new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().slice(0, 10),
  },
})

async function onCreateFiscalCore() {
  submitError.value = ''
  submitting.value = true
  try {
    await createEntreprise({
      nom: form.nom.trim(),
      ifu: form.ifu.trim(),
      rccm: form.rccm.trim(),
      regimeFiscal: form.regimeFiscal.trim(),
      adresse: form.adresse.trim(),
      paysCode: form.paysCode.trim().toUpperCase(),
      ville: form.ville.trim(),
      telephone: form.telephone.trim(),
      email: form.email.trim(),
      logoUrl: form.logoUrl.trim() || null,
      dateEffet: form.dateEffet,
      actif: true,
      etablissements: [
        {
          code: form.etablissement.code.trim(),
          nom: form.etablissement.nom.trim(),
          adresse: form.etablissement.adresse.trim(),
          ville: form.etablissement.ville.trim(),
          principal: true,
          actif: true,
        },
      ],
      certificats: [
        {
          numeroSerie: form.certificat.numeroSerie.trim(),
          autoriteEmission: form.certificat.autoriteEmission.trim(),
          dateDebutValidite: form.certificat.dateDebutValidite,
          dateFinValidite: form.certificat.dateFinValidite,
          actif: true,
        },
      ],
    })

    await entrepriseStore.refresh()
    showCreateForm.value = false
  } catch (err) {
    submitError.value = err instanceof Error ? err.message : 'Enregistrement impossible du noyau fiscal.'
  } finally {
    submitting.value = false
  }
}

// Normalise le contenu provenant du composant de sélection de fichier.
function normalizeFileInput(payload: File | File[] | null): File | null {
  if (!payload) return null
  return Array.isArray(payload) ? payload[0] ?? null : payload
}

// Convertit un fichier local en Data URL pour l'envoyer ou le prévisualiser.
function fileToDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        reject(new Error('Lecture fichier impossible'))
        return
      }
      resolve(reader.result)
    }
    reader.onerror = () => reject(new Error('Lecture fichier impossible'))
    reader.readAsDataURL(file)
  })
}

// Remplit le logo du formulaire de création à partir d'un fichier choisi.
async function onLogoFileSelected(payload: File | File[] | null) {
  const file = normalizeFileInput(payload)
  if (!file) return

  try {
    form.logoUrl = await fileToDataUrl(file)
  } catch {
    submitError.value = 'Impossible de lire le logo sélectionné.'
  }
}

// Remplit le logo du formulaire de mise à jour à partir d'un fichier choisi.
async function onLogoFileSelectedForUpdate(payload: File | File[] | null) {
  const file = normalizeFileInput(payload)
  if (!file) return

  try {
    logoUpdateForm.logoUrl = await fileToDataUrl(file)
  } catch {
    logoUpdateError.value = 'Impossible de lire le logo sélectionné.'
  }
}

// Met à jour uniquement le logo du noyau fiscal actif.
async function onUpdateEntrepriseLogo() {
  logoUpdateError.value = ''
  updatingLogo.value = true
  try {
    const activeEntreprise = entrepriseStore.activeEntreprise
    if (!activeEntreprise) {
      throw new Error('Aucune entreprise active')
    }

    // Mise à jour ciblée: seul le logo change.
    const updated = await updateEntrepriseLogo(activeEntreprise.id, logoUpdateForm.logoUrl.trim() || null)

    // Répercute le changement dans le store local.
    entrepriseStore.activeEntreprise = updated
    logoUpdateForm.logoUrl = ''
  } catch (err) {
    logoUpdateError.value = err instanceof Error ? err.message : 'Impossible de mettre à jour le logo.'
  } finally {
    updatingLogo.value = false
  }
}

onMounted(async () => {
  if (!entrepriseStore.isLoaded && !entrepriseStore.loading) {
    await entrepriseStore.loadActive()
  }
})
</script>

<style scoped>
.v-container {
  padding-left: 0;
  padding-right: 0;
}
</style>
