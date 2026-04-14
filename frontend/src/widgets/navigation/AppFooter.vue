<template>
  <!-- Pied de page piloté par les paramètres enregistrés localement. -->
  <footer class="app-footer" role="contentinfo">
    <div class="footer-brand">
      <div class="brand-name">{{ footer.settings.appName }}</div>
      <div class="brand-tagline">{{ footer.settings.tagline }}</div>
    </div>

    <div class="footer-contact">
      <div>{{ footer.settings.supportEmail }}</div>
      <div>{{ footer.settings.supportPhone }}</div>
      <div>{{ footer.settings.companyAddress }}</div>
    </div>

    <div class="footer-meta">
      <div>{{ footer.settings.legalText }}</div>
      <div>Version {{ footer.settings.version }} | {{ currentYear }}</div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { useFooterStore } from '@/features/settings/store/useFooterStore'

// Les informations affichées ici viennent du store de configuration du footer.
const currentYear = new Date().getFullYear()
const footer = useFooterStore()
</script>

<style scoped>
.app-footer {
  border-top: 1px solid rgba(10, 111, 138, 0.2);
  background:
    radial-gradient(circle at 10% 10%, rgba(10, 147, 150, 0.08), transparent 40%),
    linear-gradient(180deg, rgba(247, 251, 255, 0.98), rgba(238, 247, 255, 0.98));
  color: #153a48;
  min-height: 74px;
  padding: 0.9rem 1.25rem;
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: 0.9rem;
  font-size: 0.83rem;
}

.footer-brand,
.footer-contact,
.footer-meta {
  display: grid;
  align-content: center;
  gap: 0.2rem;
}

.brand-name {
  font-weight: 800;
  letter-spacing: 0.02em;
  font-size: 0.9rem;
}

.brand-tagline {
  color: #336072;
}

.footer-meta {
  text-align: right;
  color: #305b6c;
}

@media (max-width: 900px) {
  .app-footer {
    grid-template-columns: 1fr;
    padding: 0.75rem;
    min-height: auto;
  }

  .footer-meta {
    text-align: left;
  }
}
</style>
