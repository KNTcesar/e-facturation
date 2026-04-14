<template>
  <div>
    <!-- Tableau de bord: synthèse opérationnelle et accès rapide aux actions. -->
    <PageHeader
      title="Dashboard Fiscal"
      subtitle="Vue globale des operations de facturation et de transmission"
      icon="mdi-view-dashboard-outline"
    >
      <v-btn color="primary" prepend-icon="mdi-file-document-plus-outline" to="/factures/creer">
        Nouvelle facture
      </v-btn>
      <v-btn class="ml-2" color="secondary" variant="tonal" prepend-icon="mdi-transit-connection-variant" to="/transmissions">
        Voir transmissions
      </v-btn>
    </PageHeader>

    <v-alert v-if="error" type="error" variant="tonal" class="mb-3">{{ error }}</v-alert>

    <v-card class="hero-panel mb-4" rounded="xl" elevation="0">
      <v-card-text class="hero-layout">
        <div>
          <p class="hero-kicker">Cockpit operationnel</p>
          <h2 class="hero-title">Pilotage FEC et transmissions SECeF</h2>
          <p class="hero-subtitle">
            Supervise les factures, la qualite de transmission et la conformite fiscale depuis
            une vue unique orientee exploitation.
          </p>

          <div class="hero-badges">
            <v-chip size="small" color="success" variant="tonal">ACK acceptes: {{ acceptedCount }}</v-chip>
            <v-chip size="small" color="warning" variant="tonal">En attente: {{ transmissionsEnAttente }}</v-chip>
            <v-chip size="small" color="info" variant="tonal">Volume total: {{ transmissions.length }}</v-chip>
          </div>
        </div>

        <div class="hero-gauge">
          <v-progress-circular
            :model-value="tauxAcceptance"
            :size="120"
            :width="12"
            color="success"
          >
            <div class="gauge-value">{{ tauxAcceptance }}%</div>
          </v-progress-circular>
          <p class="gauge-label">Taux d'acceptation DGI</p>
        </div>
      </v-card-text>
    </v-card>

    <section class="kpi-grid mb-4">
      <v-card
        v-for="item in kpiCards"
        :key="item.label"
        class="kpi-card"
        rounded="xl"
        elevation="0"
      >
        <v-card-text class="pa-4">
          <div class="kpi-head">
            <div class="kpi-icon" :class="`tone-${item.tone}`">
              <v-icon :icon="item.icon" size="20" />
            </div>
            <v-chip size="x-small" variant="tonal" :color="item.chipColor">{{ item.chipText }}</v-chip>
          </div>
          <p class="kpi-number">{{ item.value }}</p>
          <p class="kpi-label">{{ item.label }}</p>
        </v-card-text>
      </v-card>
    </section>

    <!-- Fiscal core summary -->
    <FiscalSummaryCard class="mb-4" />

    <v-card class="soft-panel pa-4 mb-4" rounded="xl" elevation="0">
      <div class="d-flex align-center justify-space-between ga-2 flex-wrap mb-3">
        <h3 class="mb-0">Performance de transmission</h3>
        <v-chip size="small" :color="tauxAcceptance >= 80 ? 'success' : 'warning'" variant="tonal">
          {{ tauxAcceptance }}% acceptance
        </v-chip>
      </div>
      <div class="pipeline-grid">
        <div class="pipeline-item">
          <p class="pipeline-label">ACK acceptes</p>
          <p class="pipeline-value">{{ acceptedCount }}</p>
        </div>
        <div class="pipeline-item">
          <p class="pipeline-label">Retries planifies</p>
          <p class="pipeline-value">{{ retryCount }}</p>
        </div>
        <div class="pipeline-item">
          <p class="pipeline-label">Echecs</p>
          <p class="pipeline-value">{{ failedCount }}</p>
        </div>
      </div>
    </v-card>

    <v-row class="mt-2" density="comfortable">
      <v-col cols="12" lg="7">
        <v-card class="soft-panel pa-4 h-100" rounded="xl" elevation="0">
          <h3 class="mb-3">Dernieres factures</h3>
          <table class="data-table">
            <thead>
              <tr>
                <th>Numero</th>
                <th>Client</th>
                <th>Date</th>
                <th>TTC</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in recentInvoices" :key="row.id">
                <td class="mono">{{ row.numero }}</td>
                <td>{{ row.client?.nom ?? '-' }}</td>
                <td>{{ row.dateEmission }}</td>
                <td>{{ formatCfa(row.totalTtc) }}</td>
              </tr>
              <tr v-if="!recentInvoices.length">
                <td colspan="4" class="text-medium-emphasis">Aucune facture disponible.</td>
              </tr>
            </tbody>
          </table>
        </v-card>
      </v-col>

      <v-col cols="12" lg="5">
        <v-card class="soft-panel pa-4 h-100" rounded="xl" elevation="0">
          <h3 class="mb-3">Pipeline transmission</h3>
          <v-list bg-color="transparent">
            <v-list-item
              v-for="tx in transmissions"
              :key="tx.id"
              :title="tx.factureId"
              :subtitle="`Code: ${tx.codeRetour ?? '-'} | Retry: ${tx.retryCount}`"
            >
              <template #append>
                <v-chip
                  size="small"
                  :color="transmissionStatusUi(tx.statut).color"
                  variant="tonal"
                >
                  {{ transmissionStatusUi(tx.statut).label }}
                </v-chip>
              </template>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
// Le dashboard agrège plusieurs sources pour donner une vue d'exploitation.
import { computed, onMounted, ref } from 'vue'
import PageHeader from '@/shared/ui/PageHeader.vue'
import { transmissionStatusUi } from '@/shared/utils/status'
import { listTransmissions, type TransmissionResponse } from '@/features/transmissions/api/transmissionApi'
import { listClients } from '@/features/clients/api/clientApi'
import { listProducts } from '@/features/products/api/productApi'
import { listInvoices, type InvoiceResponse } from '@/features/invoices/api/invoiceApi'
import FiscalSummaryCard from '@/features/entreprises/components/FiscalSummaryCard.vue'

const transmissions = ref<TransmissionResponse[]>([])
const recentInvoices = ref<InvoiceResponse[]>([])
const clientsCount = ref(0)
const productsCount = ref(0)
const error = ref('')

const facturesJour = computed(() => transmissions.value.length)
const transmissionsEnAttente = computed(
  () => transmissions.value.filter((t) => t.statut === 'PENDING' || t.statut === 'RETRY_SCHEDULED').length,
)
const acceptedCount = computed(() => transmissions.value.filter((t) => t.statut === 'ACK_ACCEPTED').length)
const retryCount = computed(() => transmissions.value.filter((t) => t.statut === 'RETRY_SCHEDULED').length)
const failedCount = computed(() => transmissions.value.filter((t) => t.statut === 'FAILED').length)
const tauxAcceptance = computed(() => {
  if (!transmissions.value.length) return 0
  return Number(((acceptedCount.value / transmissions.value.length) * 100).toFixed(1))
})

// Cartes KPI calculées depuis les données métier du jour.
const kpiCards = computed(() => [
  {
    label: 'Factures du jour',
    value: facturesJour.value,
    icon: 'mdi-receipt-text-check-outline',
    tone: 'teal',
    chipColor: 'teal',
    chipText: 'Flux',
  },
  {
    label: 'Clients actifs',
    value: clientsCount.value,
    icon: 'mdi-account-group-outline',
    tone: 'sky',
    chipColor: 'info',
    chipText: 'Base',
  },
  {
    label: 'Transmissions a traiter',
    value: transmissionsEnAttente.value,
    icon: 'mdi-transit-connection-variant',
    tone: 'amber',
    chipColor: transmissionsEnAttente.value > 0 ? 'warning' : 'success',
    chipText: transmissionsEnAttente.value > 0 ? 'Action' : 'Stable',
  },
  {
    label: 'Produits catalogues',
    value: productsCount.value,
    icon: 'mdi-package-variant-closed',
    tone: 'slate',
    chipColor: 'secondary',
    chipText: 'Catalogue',
  },
])

// Chargement parallèle des indicateurs, clients, produits et factures récentes.
async function load() {
  error.value = ''
  try {
    const [txData, cData, pData, iData] = await Promise.all([
      listTransmissions(),
      listClients(),
      listProducts(),
      listInvoices(),
    ])
    transmissions.value = txData
    clientsCount.value = cData.length
    productsCount.value = pData.length
    recentInvoices.value = [...iData]
      .sort((a, b) => {
        const dateDiff = new Date(b.dateEmission).getTime() - new Date(a.dateEmission).getTime()
        if (dateDiff !== 0) return dateDiff
        return b.numero.localeCompare(a.numero)
      })
      .slice(0, 5)
  } catch {
    error.value = 'Impossible de charger les indicateurs temps reel.'
  }
}

onMounted(load)

function formatCfa(value: number) {
  return new Intl.NumberFormat('fr-FR').format(value) + ' FCFA'
}
</script>

<style scoped>
:root {
  color-scheme: light;
}

.hero-panel,
.kpi-card,
.pipeline-item {
  transition: box-shadow 180ms ease, transform 180ms ease, border-color 180ms ease;
}

.hero-panel {
  border: 1px solid rgba(21, 86, 120, 0.35);
  background:
    radial-gradient(circle at 84% 18%, rgba(249, 181, 76, 0.28), transparent 44%),
    radial-gradient(circle at 18% 88%, rgba(101, 180, 206, 0.22), transparent 48%),
    linear-gradient(142deg, #0f3f5f 0%, #0f5f80 48%, #14799b 100%);
  color: #fff;
  box-shadow: 0 16px 36px rgba(12, 53, 82, 0.22);
}

.hero-panel :deep(h2),
.hero-panel :deep(p) {
  color: #fff;
}

.hero-layout {
  display: grid;
  grid-template-columns: 1.4fr auto;
  gap: 1rem;
  align-items: center;
}

.hero-kicker {
  margin: 0;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-size: 0.78rem;
  color: rgba(232, 245, 252, 0.9);
}

.hero-title {
  margin: 0.45rem 0;
  color: #f8fcff;
  text-shadow: 0 1px 0 rgba(7, 37, 57, 0.28);
}

.hero-subtitle {
  margin: 0;
  max-width: 58ch;
  color: rgba(226, 242, 250, 0.95);
}

.hero-badges {
  margin-top: 0.9rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.hero-gauge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.45rem;
  padding: 0.45rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(231, 244, 250, 0.16);
}

.gauge-value {
  font-weight: 800;
  font-size: 1.05rem;
  color: #ffffff;
}

.gauge-label {
  margin: 0;
  color: rgba(228, 242, 249, 0.95);
  font-size: 0.82rem;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.75rem;
}

.kpi-card {
  border: 1px solid rgba(28, 91, 123, 0.14);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(245, 249, 252, 0.96));
  box-shadow: 0 8px 18px rgba(20, 57, 83, 0.08);
}

.kpi-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 24px rgba(20, 57, 83, 0.12);
  border-color: rgba(24, 103, 145, 0.22);
}

.kpi-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.7rem;
}

.kpi-icon {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  border: 1px solid transparent;
}

.kpi-number {
  margin: 0;
  color: #102c42;
  font-size: 1.7rem;
  font-weight: 800;
}

.kpi-label {
  margin: 0.3rem 0 0;
  color: #5b6f80;
  font-size: 0.88rem;
}

.tone-teal {
  color: #0f766e;
  background: rgba(15, 118, 110, 0.14);
  border-color: rgba(15, 118, 110, 0.24);
}

.tone-sky {
  color: #0b5e90;
  background: rgba(11, 94, 144, 0.13);
  border-color: rgba(11, 94, 144, 0.22);
}

.tone-amber {
  color: #b45808;
  background: rgba(213, 117, 7, 0.14);
  border-color: rgba(213, 117, 7, 0.24);
}

.tone-slate {
  color: #4b5e72;
  background: rgba(74, 94, 114, 0.14);
  border-color: rgba(74, 94, 114, 0.24);
}

.pipeline-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.7rem;
}

.pipeline-item {
  border: 1px solid rgba(26, 95, 131, 0.15);
  border-radius: 12px;
  padding: 0.8rem;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(247, 251, 254, 0.95));
  box-shadow: 0 6px 14px rgba(19, 57, 83, 0.06);
}

.pipeline-item:hover {
  border-color: rgba(24, 103, 145, 0.25);
  box-shadow: 0 10px 18px rgba(19, 57, 83, 0.1);
}

.pipeline-label {
  margin: 0;
  color: #5f7182;
  font-size: 0.83rem;
}

.pipeline-value {
  margin: 0.2rem 0 0;
  font-size: 1.28rem;
  font-weight: 800;
  color: #14324a;
}

@media (max-width: 1080px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 840px) {
  .hero-layout {
    grid-template-columns: 1fr;
  }

  .hero-gauge {
    align-items: flex-start;
  }

  .pipeline-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>