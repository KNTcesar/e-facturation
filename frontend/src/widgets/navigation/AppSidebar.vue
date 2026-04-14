<template>
  <!-- Navigation principale: accès direct aux modules fonctionnels. -->
  <v-navigation-drawer rail-expand permanent color="surface" width="240" class="side-shell">
    <div class="brand">
      <div class="brand-mark">SF</div>
      <div>
        <p class="brand-title">SFE Console</p>
        <p class="brand-subtitle">Burkina Faso</p>
      </div>
    </div>

    <v-divider class="mb-2" />

    <v-chip size="x-small" color="secondary" variant="tonal" class="mx-4 mb-3">
      ENV: DEV
    </v-chip>

    <v-list nav density="compact">
      <v-list-item
        v-for="item in menu"
        :key="item.to"
        :prepend-icon="item.icon"
        :title="item.label"
        :to="item.to"
        rounded="xl"
        color="primary"
      />
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
// Menu latéral regroupant les écrans métier principaux.
const menu = [
  { label: 'Dashboard', to: '/dashboard', icon: 'mdi-view-dashboard-outline' },
  { label: 'Clients', to: '/clients', icon: 'mdi-account-group-outline' },
  { label: 'Produits', to: '/produits', icon: 'mdi-package-variant-closed' },
  { label: 'Noyau Fiscal', to: '/noyau-fiscal', icon: 'mdi-file-certificate-outline' },
  { label: 'Créer facture', to: '/factures/creer', icon: 'mdi-file-document-plus-outline' },
  { label: 'Factures', to: '/factures', icon: 'mdi-file-document-outline' },
  { label: 'Transmissions', to: '/transmissions', icon: 'mdi-transit-connection-variant' },
  { label: 'Audit', to: '/audit', icon: 'mdi-shield-check-outline' },
  { label: 'Parametres', to: '/parametres', icon: 'mdi-cog-outline' },
]
</script>

<style scoped>
.brand {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 0.6rem;
  align-items: center;
  padding: 0.8rem 0.75rem;
}

.side-shell {
  border-right: 1px solid rgba(0, 95, 115, 0.1);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(243, 248, 251, 0.96));
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  color: #fff;
  font-weight: 700;
  font-size: 0.9rem;
  background: linear-gradient(135deg, #005f73 0%, #0a9396 100%);
}

.brand-title {
  margin: 0;
  font-weight: 700;
  font-size: 0.95rem;
}

.brand-subtitle {
  margin: 0;
  opacity: 0.7;
  font-size: 0.75rem;
}
</style>