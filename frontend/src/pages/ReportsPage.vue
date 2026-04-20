<template>
  <div>
    <PageHeader
      title="Rapports Réglementaires"
      subtitle="Génération des rapports X, Z et A"
      icon="mdi-file-chart-outline"
    />

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">
      {{ error }}
    </v-alert>

    <v-card class="soft-panel pa-4 mb-4" rounded="xl" elevation="0">
      <h3 class="mb-3">Période (optionnelle)</h3>
      <v-row dense>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="period.dateDebut"
            label="Date début"
            type="date"
            variant="outlined"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="period.dateFin"
            label="Date fin"
            type="date"
            variant="outlined"
          />
        </v-col>
        <v-col cols="12" md="4" class="d-flex align-center ga-2">
          <v-btn
            color="primary"
            prepend-icon="mdi-file-refresh-outline"
            :loading="loadingType === 'X'"
            @click="onGenerate('X')"
          >
            Générer X
          </v-btn>
          <v-btn
            color="secondary"
            prepend-icon="mdi-file-check-outline"
            :loading="loadingType === 'Z'"
            @click="onGenerate('Z')"
          >
            Générer Z
          </v-btn>
          <v-btn
            color="info"
            prepend-icon="mdi-file-document-multiple-outline"
            :loading="loadingType === 'A'"
            @click="onGenerate('A')"
          >
            Générer A
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <v-card v-if="lastReport" class="soft-panel pa-4 mb-4" rounded="xl" elevation="0">
      <div class="d-flex justify-space-between align-center mb-3">
        <h3 class="mb-0">Dernier rapport généré</h3>
        <v-chip size="small" color="primary" variant="tonal">
          {{ lastReport.typeRapport }} / {{ lastReport.natureRapport }}
        </v-chip>
      </div>

      <v-row dense class="mb-2">
        <v-col cols="12" md="4"><strong>Période:</strong> {{ lastReport.dateDebut }} - {{ lastReport.dateFin }}</v-col>
        <v-col cols="12" md="4"><strong>Acteur:</strong> {{ lastReport.generatedBy }}</v-col>
        <v-col cols="12" md="4"><strong>Généré le:</strong> {{ formatDateTime(lastReport.generatedAt) }}</v-col>
      </v-row>

      <v-row dense>
        <v-col cols="12" md="3"><strong>Factures:</strong> {{ lastReport.nombreFactures }}</v-col>
        <v-col cols="12" md="3"><strong>Total HT:</strong> {{ formatCfa(lastReport.totalHt) }}</v-col>
        <v-col cols="12" md="3"><strong>Total TVA:</strong> {{ formatCfa(lastReport.totalTva) }}</v-col>
        <v-col cols="12" md="3"><strong>Total TTC:</strong> {{ formatCfa(lastReport.totalTtc) }}</v-col>
      </v-row>

      <v-divider class="my-3" />

      <h4 class="mb-2">Factures par type</h4>
      <v-table class="table-sm mb-3">
        <thead>
          <tr>
            <th>Type</th>
            <th>Nombre</th>
            <th>HT</th>
            <th>TVA</th>
            <th>TTC</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in lastReport.facturesParType" :key="item.typeFacture">
            <td>{{ formatTypeFacture(item.typeFacture) }}</td>
            <td>{{ item.nombreFactures }}</td>
            <td>{{ formatCfa(item.totalHt) }}</td>
            <td>{{ formatCfa(item.totalTva) }}</td>
            <td>{{ formatCfa(item.totalTtc) }}</td>
          </tr>
          <tr v-if="lastReport.facturesParType.length === 0">
            <td colspan="5" class="text-medium-emphasis">Aucune donnée.</td>
          </tr>
        </tbody>
      </v-table>

      <h4 class="mb-2">Paiements par mode</h4>
      <v-table class="table-sm mb-3">
        <thead>
          <tr>
            <th>Mode</th>
            <th>Montant total</th>
            <th>Transactions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in lastReport.paiementsParMode" :key="item.modePaiement">
            <td>{{ item.modePaiement }}</td>
            <td>{{ formatCfa(item.montantTotal) }}</td>
            <td>{{ item.nombreTransactions }}</td>
          </tr>
          <tr v-if="lastReport.paiementsParMode.length === 0">
            <td colspan="3" class="text-medium-emphasis">Aucune donnée.</td>
          </tr>
        </tbody>
      </v-table>

      <div v-if="lastReport.typeRapport === 'A'">
        <h4 class="mb-2">Articles</h4>
        <v-table class="table-sm">
          <thead>
            <tr>
              <th>Code</th>
              <th>Désignation</th>
              <th>Qté nette</th>
              <th>Montant encaissé</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in lastReport.articles" :key="item.codeArticle">
              <td>{{ item.codeArticle }}</td>
              <td>{{ item.designationArticle }}</td>
              <td>{{ item.quantiteNette }}</td>
              <td>{{ formatCfa(item.montantEncaisse) }}</td>
            </tr>
            <tr v-if="lastReport.articles.length === 0">
              <td colspan="4" class="text-medium-emphasis">Aucune donnée article.</td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </v-card>

    <v-card class="soft-panel pa-4" rounded="xl" elevation="0">
      <div class="d-flex justify-space-between align-center mb-3">
        <h3 class="mb-0">Historique</h3>
        <v-btn size="small" variant="tonal" prepend-icon="mdi-refresh" :loading="historyLoading" @click="loadHistory">
          Recharger
        </v-btn>
      </div>

      <v-table class="table-sm">
        <thead>
          <tr>
            <th>Type</th>
            <th>Nature</th>
            <th>Période</th>
            <th>Généré par</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in history" :key="item.id">
            <td>{{ item.typeRapport }}</td>
            <td>{{ item.natureRapport }}</td>
            <td>{{ item.dateDebut }} - {{ item.dateFin }}</td>
            <td>{{ item.generatedBy }}</td>
            <td>{{ formatDateTime(item.generatedAt) }}</td>
          </tr>
          <tr v-if="history.length === 0">
            <td colspan="5" class="text-medium-emphasis">Aucun rapport généré.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import {
  generateReportA,
  generateReportX,
  generateReportZ,
  listReportsHistory,
  type ReportRequest,
  type ReportResponse,
  type ReportType,
} from '@/features/reports/api/reportApi'

const history = ref<ReportResponse[]>([])
const historyLoading = ref(false)
const loadingType = ref<ReportType | null>(null)
const error = ref('')
const lastReport = ref<ReportResponse | null>(null)

const period = reactive({
  dateDebut: '',
  dateFin: '',
})

function buildPayload(): ReportRequest {
  return {
    dateDebut: period.dateDebut || null,
    dateFin: period.dateFin || null,
  }
}

async function onGenerate(type: ReportType) {
  loadingType.value = type
  error.value = ''
  try {
    const payload = buildPayload()
    if (type === 'X') lastReport.value = await generateReportX(payload)
    if (type === 'Z') lastReport.value = await generateReportZ(payload)
    if (type === 'A') lastReport.value = await generateReportA(payload)
    await loadHistory()
  } catch {
    error.value = 'Impossible de générer le rapport demandé.'
  } finally {
    loadingType.value = null
  }
}

async function loadHistory() {
  historyLoading.value = true
  error.value = ''
  try {
    history.value = await listReportsHistory()
  } catch {
    error.value = 'Impossible de charger l\'historique des rapports.'
  } finally {
    historyLoading.value = false
  }
}

function formatCfa(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}

function formatDateTime(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('fr-BF')
}

function formatTypeFacture(value: string) {
  const labels: Record<string, string> = {
    FV: 'FV - Facture de vente',
    FT: 'FT - Facture d\'acompte ou d\'avance',
    FA: 'FA - Facture d\'avoir',
    EV: 'EV - Facture de vente à l\'exportation',
    ET: 'ET - Facture d\'acompte à l\'exportation',
    EA: 'EA - Facture d\'avoir à l\'exportation',
  }
  return labels[value] ?? value
}

onMounted(loadHistory)
</script>

<style scoped>
.table-sm {
  font-size: 0.9rem;
}

.table-sm :deep(th),
.table-sm :deep(td) {
  padding: 8px;
}
</style>
