<template>
  <!-- Barre du haut: recherche rapide, notifications et session active. -->
  <v-app-bar flat color="transparent" density="comfortable" class="topbar" height="72">
    <v-app-bar-title class="title">Plateforme SFE</v-app-bar-title>

    <v-text-field
      class="quick-search mr-3"
      prepend-inner-icon="mdi-magnify"
      placeholder="Rechercher facture, IFU, client"
      hide-details
      density="compact"
      variant="solo"
      flat
    />

    <template #append>
      <v-btn icon="mdi-bell-outline" variant="text" class="mr-1" />
      <v-chip variant="tonal" color="primary" class="mr-2">
        {{ authStore.username || 'Utilisateur' }}
      </v-chip>

      <v-btn icon="mdi-logout" variant="text" @click="onLogout" />
    </template>
  </v-app-bar>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/features/auth/store/useAuthStore'

// La topbar affiche l'identité courante et permet de fermer la session.
const authStore = useAuthStore()
const router = useRouter()

function onLogout() {
  // Déconnexion locale puis retour vers l'écran de login.
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.topbar {
  margin: 0.15rem 0.5rem 0;
}

.title {
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 600;
}

.quick-search {
  max-width: 360px;
}

@media (max-width: 920px) {
  .quick-search {
    display: none;
  }
}
</style>