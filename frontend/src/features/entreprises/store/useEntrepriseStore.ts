/**
 * Store du noyau fiscal commun.
 * Il garde le profil fiscal actif en mémoire pour les écrans de facturation.
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getActiveEntreprise } from '../api/entrepriseApi'
import type { EntrepriseResponse } from '../api/types'

export const useEntrepriseStore = defineStore('entreprise', () => {
  // Profil fiscal actuellement utilisé par l'application.
  const activeEntreprise = ref<EntrepriseResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Évite de recharger le profil si on l'a déjà en mémoire.
  const isLoaded = computed(() => activeEntreprise.value !== null)

  // Charge le profil actif une seule fois tant qu'il est disponible.
  const loadActive = async () => {
    if (isLoaded.value) return // Don't reload if already loaded

    loading.value = true
    error.value = null

    try {
      activeEntreprise.value = await getActiveEntreprise()
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err)
      console.error('Failed to load active entreprise:', err)
    } finally {
      loading.value = false
    }
  }

  // Rafraîchit le profil fiscal depuis l'API.
  const refresh = async () => {
    loading.value = true
    error.value = null

    try {
      activeEntreprise.value = await getActiveEntreprise()
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err)
      console.error('Failed to refresh entreprise:', err)
    } finally {
      loading.value = false
    }
  }

  // Réinitialise l'état local sans toucher à l'API.
  const reset = () => {
    activeEntreprise.value = null
    error.value = null
  }

  return {
    // État
    activeEntreprise,
    loading,
    error,

    // Indicateur dérivé
    isLoaded,

    // Actions
    loadActive,
    refresh,
    reset,
  }
})
