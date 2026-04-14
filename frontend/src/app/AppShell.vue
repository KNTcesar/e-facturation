<template>
  <!-- Le shell affiche la navigation seulement quand l'utilisateur est authentifié. -->
  <v-app>
    <v-layout class="app-layout">
      <!-- Barre latérale principale du produit. -->
      <AppSidebar v-if="authStore.isAuthenticated" />

      <v-main class="main-content">
        <!-- Barre haute avec actions rapides et session active. -->
        <AppTopbar v-if="authStore.isAuthenticated" />

        <v-container class="page-container" fluid>
          <!-- Le contenu de la route est injecté ici avec une transition légère. -->
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </v-container>

        <AppFooter />
      </v-main>
    </v-layout>
  </v-app>
</template>

<script setup lang="ts">
// Le shell dépend de l'état d'authentification pour afficher les zones privées.
import { useAuthStore } from '@/features/auth/store/useAuthStore'
import AppFooter from '@/widgets/navigation/AppFooter.vue'
import AppSidebar from '@/widgets/navigation/AppSidebar.vue'
import AppTopbar from '@/widgets/navigation/AppTopbar.vue'

const authStore = useAuthStore()
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
}

.main-content {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-container {
  flex: 1;
  padding: 1rem 1.25rem 1.25rem;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 960px) {
  .page-container {
    padding: 0.75rem;
  }
}
</style>