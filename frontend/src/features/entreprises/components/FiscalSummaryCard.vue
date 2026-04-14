<template>
  <v-card class="soft-panel" rounded="xl" elevation="0">
    <v-card-title class="d-flex align-center gap-2 pb-2">
      <v-icon icon="mdi-building" color="primary"></v-icon>
      Profil Fiscal Commun
    </v-card-title>

    <v-divider class="opacity-50" />

    <v-card-text class="pt-3">
      <v-row dense v-if="entrepriseStore.activeEntreprise">
        <v-col cols="12" md="6">
          <div class="fiscal-info">
            <div class="label">Entreprise</div>
            <div class="value">{{ entrepriseStore.activeEntreprise.nom }}</div>
          </div>
        </v-col>
        <v-col cols="12" md="6">
          <div class="fiscal-info">
            <div class="label">Régime</div>
            <div class="value">{{ entrepriseStore.activeEntreprise.regimeFiscal }}</div>
          </div>
        </v-col>
      </v-row>

      <v-row dense v-if="entrepriseStore.activeEntreprise" class="mt-2">
        <v-col cols="6" sm="3">
          <v-chip
            :color="entrepriseStore.activeEntreprise.actif ? 'success' : 'error'"
            size="small"
            label
          >
            {{ entrepriseStore.activeEntreprise.actif ? 'Actif' : 'Inactif' }}
          </v-chip>
        </v-col>
        <v-col cols="6" sm="3">
          <v-chip size="small" variant="tonal" color="info">
            Étab: {{ entrepriseStore.activeEntreprise.etablissements.length }}
          </v-chip>
        </v-col>
        <v-col cols="6" sm="3">
          <v-chip size="small" variant="tonal" color="warning">
            Cert: {{ entrepriseStore.activeEntreprise.certificats.length }}
          </v-chip>
        </v-col>
        <v-col cols="6" sm="3" class="text-right">
          <router-link to="/noyau-fiscal">
            <v-btn size="x-small" icon="mdi-open-in-new" variant="text" />
          </router-link>
        </v-col>
      </v-row>

      <div v-else-if="entrepriseStore.loading" class="text-center py-3">
        <v-progress-circular indeterminate size="24" class="mx-auto"></v-progress-circular>
      </div>

      <div v-else class="text-center text-muted py-3">
        Aucun profil fiscal
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { useEntrepriseStore } from '../store/useEntrepriseStore'
import { onMounted } from 'vue'

const entrepriseStore = useEntrepriseStore()

onMounted(() => {
  if (!entrepriseStore.isLoaded && !entrepriseStore.loading) {
    entrepriseStore.loadActive()
  }
})
</script>

<style scoped>
.fiscal-info {
  padding: 8px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 6px;
}

.fiscal-info .label {
  font-size: 0.75rem;
  opacity: 0.6;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.fiscal-info .value {
  font-weight: 600;
  font-size: 0.95rem;
}
</style>
