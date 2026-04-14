import { defineStore } from 'pinia'
import { ref } from 'vue'

// Paramètres affichés dans le pied de page et modifiables depuis l'interface.
export type FooterSettings = {
  appName: string
  tagline: string
  supportEmail: string
  supportPhone: string
  companyAddress: string
  legalText: string
  version: string
}

// Clé de persistance locale pour garder les préférences du footer.
const STORAGE_KEY = 'sfe_footer_settings'

// Valeurs de repli si rien n'est encore stocké localement.
const defaultSettings: FooterSettings = {
  appName: 'Plateforme SFE',
  tagline: 'E-Facturation DGI Burkina Faso',
  supportEmail: 'support@sfe.bf',
  supportPhone: '+226 25 00 00 00',
  companyAddress: 'Ouagadougou, Burkina Faso',
  legalText: 'Conforme aux exigences DGI / SECeF',
  version: '1.0.0',
}

export const useFooterStore = defineStore('footer-settings', () => {
  const settings = ref<FooterSettings>({ ...defaultSettings })

  // Lecture depuis localStorage avec fusion sur les valeurs par défaut.
  function load() {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return

    try {
      const parsed = JSON.parse(raw) as Partial<FooterSettings>
      settings.value = {
        ...defaultSettings,
        ...parsed,
      }
    } catch {
      settings.value = { ...defaultSettings }
    }
  }

  // Sauvegarde immédiate des paramètres du footer.
  function save() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(settings.value))
  }

  // Réinitialise le pied de page vers les valeurs standard.
  function reset() {
    settings.value = { ...defaultSettings }
    save()
  }

  load()

  return {
    settings,
    save,
    reset,
  }
})
