<template>
  <div>
    <!-- Page de réglages: backend, conformité, footer et traces récentes. -->
    <PageHeader title="Parametres" subtitle="Configuration API, roles et preferences" />

    <v-row dense>
      <v-col cols="12" md="6">
        <v-card class="soft-panel pa-4 h-100" rounded="xl" elevation="0">
          <h3 class="mb-3">API Backend</h3>
          <v-text-field label="Base URL" model-value="http://localhost:8080" variant="outlined" />
          <v-select
            label="Mode"
            variant="outlined"
            :items="['development', 'preproduction', 'production']"
            model-value="development"
          />
          <v-switch label="Healthcheck automatique" color="primary" model-value />
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card class="soft-panel pa-4 h-100" rounded="xl" elevation="0">
          <h3 class="mb-3">Conformite FEC</h3>
          <v-alert
            :type="verification?.valid ? 'success' : 'warning'"
            variant="tonal"
            class="mb-3"
          >
            {{ verification?.message || 'Verification non lancee' }}
          </v-alert>
          <div class="d-flex align-center ga-2 mb-2">
            <v-btn
              color="primary"
              prepend-icon="mdi-shield-check-outline"
              :loading="loading"
              @click="loadAudit"
            >
              Verifier chainage
            </v-btn>
            <v-chip size="small" variant="tonal" color="secondary">
              Entrees: {{ verification?.totalEntries ?? 0 }}
            </v-chip>
          </div>
          <p class="kpi-label">Sprint S3: piste d'audit append-only chainee</p>
        </v-card>
      </v-col>
    </v-row>

    <v-card class="soft-panel pa-4 mt-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-3 mb-3">
        <h3 class="mb-0">Pied de page application</h3>
        <v-chip size="small" variant="tonal" color="primary">Personnalisable</v-chip>
      </div>

      <v-alert v-if="footerSaved" type="success" variant="tonal" class="mb-3">
        Informations du pied de page enregistrées.
      </v-alert>

      <v-row dense>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.appName" label="Nom application" variant="outlined" />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.tagline" label="Signature" variant="outlined" />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.version" label="Version" variant="outlined" />
        </v-col>
      </v-row>

      <v-row dense>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.supportEmail" label="Email support" variant="outlined" />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.supportPhone" label="Téléphone support" variant="outlined" />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field v-model="footerForm.companyAddress" label="Adresse" variant="outlined" />
        </v-col>
      </v-row>

      <v-textarea
        v-model="footerForm.legalText"
        label="Mention légale / conformité"
        variant="outlined"
        rows="2"
        auto-grow
      />

      <div class="d-flex justify-end ga-2 mt-2">
        <v-btn variant="text" @click="onResetFooter">Réinitialiser</v-btn>
        <v-btn color="primary" prepend-icon="mdi-content-save-outline" @click="onSaveFooter">
          Enregistrer
        </v-btn>
      </div>
    </v-card>

    <v-card class="soft-panel pa-4 mt-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-3 mb-3">
        <h3 class="mb-0">Gestion des utilisateurs</h3>
        <v-btn
          size="small"
          variant="tonal"
          color="secondary"
          prepend-icon="mdi-refresh"
          :loading="usersLoading"
          @click="loadUsersAdmin"
        >
          Recharger
        </v-btn>
      </div>

      <v-alert v-if="usersError" type="error" variant="tonal" class="mb-3">
        {{ usersError }}
      </v-alert>

      <v-alert v-if="userCreated" type="success" variant="tonal" class="mb-3">
        Utilisateur créé avec succès.
      </v-alert>

      <v-row dense>
        <v-col cols="12" lg="7">
          <table class="data-table">
            <thead>
              <tr>
                <th>Identifiant</th>
                <th>Nom complet</th>
                <th>Statut</th>
                <th>Rôles</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in users" :key="u.id">
                <td class="mono">{{ u.username }}</td>
                <td>{{ u.nomComplet }}</td>
                <td>
                  <v-chip size="x-small" :color="u.actif ? 'success' : 'error'" variant="tonal">
                    {{ u.actif ? 'Actif' : 'Inactif' }}
                  </v-chip>
                </td>
                <td>{{ u.roles.join(', ') }}</td>
                <td>
                  <div class="d-flex ga-1 flex-wrap">
                    <v-btn
                      size="x-small"
                      variant="tonal"
                      :color="u.actif ? 'warning' : 'success'"
                      :loading="actionUserId === u.id && actionType === 'toggle'"
                      @click="onToggleActivation(u)"
                    >
                      {{ u.actif ? 'Désactiver' : 'Activer' }}
                    </v-btn>
                    <v-btn
                      size="x-small"
                      variant="tonal"
                      color="info"
                      :loading="actionUserId === u.id && actionType === 'reset'"
                      @click="onOpenResetPassword(u)"
                    >
                      Reset MDP
                    </v-btn>
                    <v-btn
                      size="x-small"
                      variant="tonal"
                      color="error"
                      :loading="actionUserId === u.id && actionType === 'delete'"
                      @click="onDeleteUser(u)"
                    >
                      Supprimer
                    </v-btn>
                  </div>
                </td>
              </tr>
              <tr v-if="users.length === 0">
                <td colspan="5" class="text-center py-4">Aucun utilisateur disponible.</td>
              </tr>
            </tbody>
          </table>
        </v-col>

        <v-col cols="12" lg="5">
          <v-card variant="outlined" rounded="lg" class="pa-3">
            <h4 class="mb-3">Créer un utilisateur</h4>

            <v-form @submit.prevent="onCreateUser">
              <v-text-field
                v-model="createUserForm.username"
                label="Identifiant"
                variant="outlined"
                density="comfortable"
              />

              <v-text-field
                v-model="createUserForm.nomComplet"
                label="Nom complet"
                variant="outlined"
                density="comfortable"
              />

              <v-text-field
                v-model="createUserForm.password"
                label="Mot de passe"
                type="password"
                variant="outlined"
                density="comfortable"
              />

              <v-select
                v-model="createUserForm.roles"
                :items="roleItems"
                label="Rôles"
                variant="outlined"
                density="comfortable"
                multiple
                chips
              />

              <div class="d-flex justify-end mt-2">
                <v-btn type="submit" color="primary" :loading="createUserLoading">
                  Créer
                </v-btn>
              </div>
            </v-form>
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <v-dialog v-model="openResetPassword" max-width="520">
      <v-card rounded="xl">
        <v-card-title>Réinitialiser mot de passe</v-card-title>
        <v-card-text>
          <p class="mb-3">Utilisateur: <strong>{{ resetPasswordTarget?.username ?? '-' }}</strong></p>
          <v-text-field
            v-model="resetPasswordForm.newPassword"
            label="Nouveau mot de passe"
            type="password"
            variant="outlined"
          />
          <div class="d-flex justify-end ga-2 mt-2">
            <v-btn variant="text" @click="openResetPassword = false">Annuler</v-btn>
            <v-btn color="primary" :loading="actionType === 'reset' && actionUserId === resetPasswordTarget?.id" @click="onConfirmResetPassword">
              Confirmer
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-card class="soft-panel pa-4 mt-4" rounded="xl" elevation="0">
      <h3 class="mb-3">Dernieres traces audit</h3>
      <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>
      <table class="data-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Action</th>
            <th>Entite</th>
            <th>Acteur</th>
            <th>Date</th>
            <th>Hash</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in entries" :key="row.id">
            <td class="mono">{{ row.sequenceNumber ?? '-' }}</td>
            <td>{{ row.action }}</td>
            <td>{{ row.entite }}</td>
            <td>{{ row.acteur }}</td>
            <td>{{ formatDate(row.createdAt) }}</td>
            <td class="mono">{{ shortHash(row.entryHash) }}</td>
          </tr>
        </tbody>
      </table>
    </v-card>
  </div>
</template>

<script setup lang="ts">
// Les réglages se concentrent sur la configuration locale et la visibilité de conformité.
import { computed, onMounted, reactive, ref } from 'vue'
import {
  listAuditEntries,
  verifyAuditChain,
  type AuditChainVerificationResponse,
  type AuditEntryResponse,
} from '@/features/audit/api/auditApi'
import {
  createAdminUser,
  deleteAdminUser,
  listAdminRoles,
  listAdminUsers,
  resetAdminUserPassword,
  setAdminUserActivation,
  type AdminRoleResponse,
  type AdminUserResponse,
} from '@/features/settings/api/userAdminApi'
import { useFooterStore } from '@/features/settings/store/useFooterStore'
import PageHeader from '@/shared/ui/PageHeader.vue'

const footerStore = useFooterStore()
const entries = ref<AuditEntryResponse[]>([])
const verification = ref<AuditChainVerificationResponse | null>(null)
const loading = ref(false)
const error = ref('')
const footerSaved = ref(false)

const users = ref<AdminUserResponse[]>([])
const roles = ref<AdminRoleResponse[]>([])
const usersLoading = ref(false)
const createUserLoading = ref(false)
const usersError = ref('')
const userCreated = ref(false)
const actionUserId = ref<string | null>(null)
const actionType = ref<'toggle' | 'reset' | 'delete' | null>(null)
const openResetPassword = ref(false)
const resetPasswordTarget = ref<AdminUserResponse | null>(null)

const resetPasswordForm = reactive({
  newPassword: '',
})

const createUserForm = reactive({
  username: '',
  nomComplet: '',
  password: '',
  roles: [] as string[],
})

const roleItems = computed(() => roles.value.map((r) => r.code))

const footerForm = reactive({
  appName: footerStore.settings.appName,
  tagline: footerStore.settings.tagline,
  supportEmail: footerStore.settings.supportEmail,
  supportPhone: footerStore.settings.supportPhone,
  companyAddress: footerStore.settings.companyAddress,
  legalText: footerStore.settings.legalText,
  version: footerStore.settings.version,
})

function onSaveFooter() {
  // Sauvegarde immédiate du pied de page dans le stockage local.
  footerStore.settings = {
    appName: footerForm.appName.trim(),
    tagline: footerForm.tagline.trim(),
    supportEmail: footerForm.supportEmail.trim(),
    supportPhone: footerForm.supportPhone.trim(),
    companyAddress: footerForm.companyAddress.trim(),
    legalText: footerForm.legalText.trim(),
    version: footerForm.version.trim(),
  }
  footerStore.save()
  footerSaved.value = true
  setTimeout(() => {
    footerSaved.value = false
  }, 1800)
}

function onResetFooter() {
  // Réinitialise les valeurs affichées dans le formulaire.
  footerStore.reset()
  footerForm.appName = footerStore.settings.appName
  footerForm.tagline = footerStore.settings.tagline
  footerForm.supportEmail = footerStore.settings.supportEmail
  footerForm.supportPhone = footerStore.settings.supportPhone
  footerForm.companyAddress = footerStore.settings.companyAddress
  footerForm.legalText = footerStore.settings.legalText
  footerForm.version = footerStore.settings.version
}

// Charge les rôles et les utilisateurs pour la section administration.
async function loadUsersAdmin() {
  usersLoading.value = true
  usersError.value = ''
  try {
    const [usersData, rolesData] = await Promise.all([listAdminUsers(), listAdminRoles()])
    users.value = usersData
    roles.value = rolesData
  } catch {
    usersError.value = "Impossible de charger la gestion des utilisateurs. Vérifie les droits ADMIN."
  } finally {
    usersLoading.value = false
  }
}

// Crée un compte utilisateur avec les rôles sélectionnés.
async function onCreateUser() {
  userCreated.value = false
  usersError.value = ''

  if (!createUserForm.username.trim() || !createUserForm.nomComplet.trim() || !createUserForm.password.trim()) {
    usersError.value = 'Identifiant, nom complet et mot de passe sont obligatoires.'
    return
  }

  if (createUserForm.roles.length === 0) {
    usersError.value = 'Sélectionne au moins un rôle.'
    return
  }

  createUserLoading.value = true
  try {
    const created = await createAdminUser({
      username: createUserForm.username.trim(),
      nomComplet: createUserForm.nomComplet.trim(),
      password: createUserForm.password,
      roles: [...createUserForm.roles],
    })
    users.value = [created, ...users.value]

    createUserForm.username = ''
    createUserForm.nomComplet = ''
    createUserForm.password = ''
    createUserForm.roles = []

    userCreated.value = true
    setTimeout(() => {
      userCreated.value = false
    }, 1800)
  } catch {
    usersError.value = "Création impossible. Vérifie que ton compte courant a le rôle ADMIN."
  } finally {
    createUserLoading.value = false
  }
}

async function onToggleActivation(user: AdminUserResponse) {
  usersError.value = ''
  actionUserId.value = user.id
  actionType.value = 'toggle'
  try {
    const updated = await setAdminUserActivation(user.id, !user.actif)
    users.value = users.value.map((u) => (u.id === user.id ? updated : u))
  } catch {
    usersError.value = 'Activation/désactivation impossible pour cet utilisateur.'
  } finally {
    actionUserId.value = null
    actionType.value = null
  }
}

function onOpenResetPassword(user: AdminUserResponse) {
  usersError.value = ''
  resetPasswordTarget.value = user
  resetPasswordForm.newPassword = ''
  openResetPassword.value = true
}

async function onConfirmResetPassword() {
  const target = resetPasswordTarget.value
  if (!target) return
  if (!resetPasswordForm.newPassword.trim()) {
    usersError.value = 'Le nouveau mot de passe est obligatoire.'
    return
  }

  usersError.value = ''
  actionUserId.value = target.id
  actionType.value = 'reset'
  try {
    await resetAdminUserPassword(target.id, resetPasswordForm.newPassword)
    openResetPassword.value = false
    resetPasswordForm.newPassword = ''
    userCreated.value = true
    setTimeout(() => {
      userCreated.value = false
    }, 1800)
  } catch {
    usersError.value = 'Réinitialisation du mot de passe impossible.'
  } finally {
    actionUserId.value = null
    actionType.value = null
  }
}

async function onDeleteUser(user: AdminUserResponse) {
  const confirmed = window.confirm(`Supprimer l'utilisateur ${user.username} ?`)
  if (!confirmed) return

  usersError.value = ''
  actionUserId.value = user.id
  actionType.value = 'delete'
  try {
    await deleteAdminUser(user.id)
    users.value = users.value.filter((u) => u.id !== user.id)
  } catch {
    usersError.value = 'Suppression impossible pour cet utilisateur.'
  } finally {
    actionUserId.value = null
    actionType.value = null
  }
}

// Vérifie la chaîne d'audit et récupère les dernières traces.
async function loadAudit() {
  loading.value = true
  error.value = ''
  try {
    const [verificationData, entryData] = await Promise.all([verifyAuditChain(), listAuditEntries()])
    verification.value = verificationData
    entries.value = entryData.slice(-15).reverse()
  } catch {
    error.value = "Impossible de charger l'etat du journal d'audit."
  } finally {
    loading.value = false
  }
}

// Hash abrégé pour un affichage compact.
function shortHash(value: string | null) {
  if (!value) return '-'
  return `${value.slice(0, 12)}...`
}

// Lecture côté humain de la date de création.
function formatDate(value: string) {
  return new Date(value).toLocaleString('fr-FR')
}

onMounted(async () => {
  await Promise.all([loadAudit(), loadUsersAdmin()])
})
</script>