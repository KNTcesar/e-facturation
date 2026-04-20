<template>
  <div class="entreprise-display">
    <!-- Header with fiscal company info -->
    <v-card class="mb-4">
      <v-card-title class="d-flex align-center gap-2">
        <v-icon icon="mdi-building" color="primary"></v-icon>
        Profil Fiscal Commun
        <v-chip
          v-if="entrepriseStore.activeEntreprise?.actif"
          size="small"
          color="success"
          label
        >
          Actif
        </v-chip>
        <v-chip v-else-if="entrepriseStore.activeEntreprise" size="small" color="error" label>
          Inactif
        </v-chip>
      </v-card-title>

      <v-card-text v-if="entrepriseStore.activeEntreprise" class="pt-0">
        <div v-if="entrepriseStore.activeEntreprise.logoUrl" class="mb-3">
          <strong>Logo:</strong>
          <div class="mt-2">
            <img
              :src="entrepriseStore.activeEntreprise.logoUrl"
              alt="Logo entreprise"
              class="entreprise-logo"
            />
          </div>
        </div>

        <!-- Company identification -->
        <div class="row mb-3">
          <div class="col-md-6">
            <strong>Nom:</strong>
            {{ entrepriseStore.activeEntreprise.nom }}
          </div>
          <div class="col-md-6">
            <strong>Régime Fiscal:</strong>
            {{ entrepriseStore.activeEntreprise.regimeFiscal }}
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-3">
            <strong>IFU:</strong>
            <code>{{ entrepriseStore.activeEntreprise.ifu }}</code>
          </div>
          <div class="col-md-3">
            <strong>RCCM:</strong>
            <code>{{ entrepriseStore.activeEntreprise.rccm }}</code>
          </div>
          <div class="col-md-3">
            <strong>Pays:</strong>
            {{ entrepriseStore.activeEntreprise.paysCode }}
          </div>
          <div class="col-md-3">
            <strong>Ville:</strong>
            {{ entrepriseStore.activeEntreprise.ville }}
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <strong>Adresse:</strong>
            {{ entrepriseStore.activeEntreprise.adresse }}
          </div>
          <div class="col-md-3">
            <strong>Téléphone:</strong>
            {{ entrepriseStore.activeEntreprise.telephone }}
          </div>
          <div class="col-md-3">
            <strong>Email:</strong>
            {{ entrepriseStore.activeEntreprise.email }}
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <strong>Service impôts:</strong>
            {{ entrepriseStore.activeEntreprise.serviceImpotRattachement }}
          </div>
          <div class="col-md-6">
            <strong>Date d'Effet:</strong>
            {{ formatDate(entrepriseStore.activeEntreprise.dateEffet) }}
          </div>
        </div>
      </v-card-text>

      <v-card-text v-else class="text-center text-muted py-8">
        <v-progress-circular
          v-if="entrepriseStore.loading"
          indeterminate
          color="primary"
          class="mb-2"
        ></v-progress-circular>
        <div v-if="entrepriseStore.loading">Chargement du profil fiscal...</div>
        <div v-else>Aucun profil fiscal disponible</div>
      </v-card-text>
    </v-card>

    <!-- Establishments section -->
    <v-card v-if="entrepriseStore.activeEntreprise" class="mb-4">
      <v-card-title class="d-flex align-center gap-2">
        <v-icon icon="mdi-store" color="info"></v-icon>
        Établissements ({{ entrepriseStore.activeEntreprise.etablissements.length }})
      </v-card-title>

      <v-card-text class="pt-0">
        <v-table v-if="entrepriseStore.activeEntreprise.etablissements.length > 0" class="table-sm">
          <thead>
            <tr>
              <th>Code</th>
              <th>Nom</th>
              <th>Ville</th>
              <th>Type</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="etab in entrepriseStore.activeEntreprise.etablissements" :key="etab.id">
              <td><code>{{ etab.code }}</code></td>
              <td>{{ etab.nom }}</td>
              <td>{{ etab.ville }}</td>
              <td>
                <v-chip
                  v-if="etab.principal"
                  size="x-small"
                  color="warning"
                  label
                >
                  Principal
                </v-chip>
                <v-chip v-else size="x-small" color="default" label>
                  Secondaire
                </v-chip>
              </td>
              <td>
                <v-chip
                  :color="etab.actif ? 'success' : 'error'"
                  size="x-small"
                  label
                >
                  {{ etab.actif ? 'Actif' : 'Inactif' }}
                </v-chip>
              </td>
            </tr>
          </tbody>
        </v-table>
        <div v-else class="text-center text-muted py-4">
          Aucun établissement
        </div>
      </v-card-text>
    </v-card>

    <!-- Certificates section -->
    <v-card v-if="entrepriseStore.activeEntreprise">
      <v-card-title class="d-flex align-center gap-2">
        <v-icon icon="mdi-certificate" color="success"></v-icon>
        Certificats Fiscaux ({{ entrepriseStore.activeEntreprise.certificats.length }})
      </v-card-title>

      <v-card-text class="pt-0">
        <v-table v-if="entrepriseStore.activeEntreprise.certificats.length > 0" class="table-sm">
          <thead>
            <tr>
              <th>N° Série</th>
              <th>Autorité</th>
              <th>Début Validité</th>
              <th>Fin Validité</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cert in entrepriseStore.activeEntreprise.certificats" :key="cert.id">
              <td><code>{{ cert.numeroSerie }}</code></td>
              <td>{{ cert.autoriteEmission }}</td>
              <td>{{ formatDate(cert.dateDebutValidite) }}</td>
              <td>{{ formatDate(cert.dateFinValidite) }}</td>
              <td>
                <v-chip
                  :color="cert.actif ? 'success' : 'error'"
                  size="x-small"
                  label
                >
                  {{ cert.actif ? 'Actif' : 'Expiré' }}
                </v-chip>
              </td>
            </tr>
          </tbody>
        </v-table>
        <div v-else class="text-center text-muted py-4">
          Aucun certificat
        </div>
      </v-card-text>
    </v-card>

    <v-card v-if="entrepriseStore.activeEntreprise" class="mt-4">
      <v-card-title class="d-flex align-center gap-2">
        <v-icon icon="mdi-bank" color="primary"></v-icon>
        Comptes bancaires ({{ entrepriseStore.activeEntreprise.comptesBancaires.length }})
      </v-card-title>

      <v-card-text class="pt-0">
        <v-table v-if="entrepriseStore.activeEntreprise.comptesBancaires.length > 0" class="table-sm">
          <thead>
            <tr>
              <th>Référence</th>
              <th>Banque</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="compte in entrepriseStore.activeEntreprise.comptesBancaires" :key="compte.id">
              <td><code>{{ compte.referenceCompte }}</code></td>
              <td>{{ compte.banque || '-' }}</td>
              <td>
                <v-chip
                  :color="compte.actif ? 'success' : 'error'"
                  size="x-small"
                  label
                >
                  {{ compte.actif ? 'Actif' : 'Inactif' }}
                </v-chip>
              </td>
            </tr>
          </tbody>
        </v-table>
        <div v-else class="text-center text-muted py-4">
          Aucun compte bancaire
        </div>
      </v-card-text>
    </v-card>

    <!-- Error message -->
    <v-alert
      v-if="entrepriseStore.error"
      type="error"
      class="mt-4"
      dismissible
      @update:model-value="entrepriseStore.error = null"
    >
      {{ entrepriseStore.error }}
    </v-alert>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useEntrepriseStore } from '../store/useEntrepriseStore'

const entrepriseStore = useEntrepriseStore()

const formatDate = (dateStr: string): string => {
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('fr-BF', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  } catch {
    return dateStr
  }
}

onMounted(() => {
  // Load active entreprise if not already loaded
  if (!entrepriseStore.isLoaded && !entrepriseStore.loading) {
    entrepriseStore.loadActive()
  }
})
</script>

<style scoped>
.entreprise-display {
  padding: 16px;
}

code {
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 0.875rem;
}

.table-sm {
  font-size: 0.875rem;
}

.table-sm :deep(th),
.table-sm :deep(td) {
  padding: 8px;
}

.entreprise-logo {
  max-width: 220px;
  max-height: 100px;
  object-fit: contain;
  border: 1px solid #e1e7f2;
  border-radius: 8px;
  background: #fff;
  padding: 6px;
}

.row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.col-md-3,
.col-md-6 {
  flex: 1;
  min-width: 250px;
}

@media (max-width: 960px) {
  .col-md-3,
  .col-md-6 {
    min-width: 100%;
    flex: 100%;
  }
}
</style>
