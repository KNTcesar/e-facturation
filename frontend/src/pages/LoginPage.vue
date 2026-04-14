<template>
  <!-- Écran de connexion minimaliste pour accéder au cockpit fiscal. -->
  <v-container fluid class="login-wrap">
    <v-row class="fill-height" align="center" justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card class="login-card" rounded="xl" elevation="0">
          <v-row no-gutters>
            <v-col cols="12" md="6" class="intro pa-8">
              <h1>Bienvenue sur SFE</h1>
              <p class="mt-4">
                Tableau de bord conforme FEC pour la gestion des factures,
                transmissions SECeF et controle fiscal continu.
              </p>
            </v-col>

            <v-col cols="12" md="6" class="pa-8">
              <h2>Connexion</h2>
              <v-form class="mt-6" @submit.prevent="onSubmit">
                <v-text-field v-model="form.username" label="Utilisateur" variant="outlined" />
                <v-text-field
                  v-model="form.password"
                  label="Mot de passe"
                  type="password"
                  variant="outlined"
                />
                <v-alert v-if="error" type="error" variant="tonal" class="mb-3">
                  {{ error }}
                </v-alert>
                <v-btn type="submit" color="primary" size="large" block :loading="authStore.loading">
                  Se connecter
                </v-btn>
              </v-form>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// Le formulaire pré-remplit des identifiants de démo pour accélérer la lecture.
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/features/auth/store/useAuthStore'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const error = ref('')

const form = reactive({
  username: 'admin',
  password: 'admin123',
})

async function onSubmit() {
  // Connexion puis redirection vers la page demandée ou le dashboard.
  error.value = ''
  try {
    await authStore.login(form)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    router.push(redirect)
  } catch {
    error.value = 'Identifiants invalides ou API indisponible.'
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  background:
    radial-gradient(circle at 10% 10%, rgba(10, 147, 150, 0.15), transparent 40%),
    radial-gradient(circle at 90% 90%, rgba(238, 155, 0, 0.2), transparent 45%);
}

.login-card {
  overflow: hidden;
  border: 1px solid rgba(0, 95, 115, 0.1);
}

.intro {
  background: linear-gradient(160deg, #005f73 0%, #0a9396 100%);
  color: #fff;
}

.intro h1,
.intro p {
  color: #fff;
}
</style>