<template>
  <!-- En-tête commun pour standardiser les titres, sous-titres et actions. -->
  <div class="page-header">
    <div class="heading-wrap">
      <div v-if="icon" class="title-icon" aria-hidden="true">
        <v-icon :icon="icon" size="20" />
      </div>
      <div>
        <h1 class="title">{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
      </div>
    </div>
    <div class="actions">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
// API minimale: le composant accepte un titre, un sous-titre et une icône optionnelle.
defineProps<{
  title: string
  subtitle: string
  icon?: string
}>()
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: start;
  margin-bottom: 1rem;
}

.heading-wrap {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.title-icon {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  color: #0a6f8a;
  background: rgba(10, 111, 138, 0.12);
  border: 1px solid rgba(10, 111, 138, 0.2);
  margin-top: 2px;
}

.title {
  margin: 0;
  font-size: 1.45rem;
}

.subtitle {
  margin: 0.35rem 0 0;
  opacity: 0.75;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

@media (max-width: 740px) {
  .page-header {
    flex-direction: column;
  }
}
</style>