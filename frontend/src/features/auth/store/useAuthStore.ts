import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { http } from '@/shared/api/http'

// Données attendues par l'écran de connexion.
type LoginPayload = {
  username: string
  password: string
}

// Réponse minimale renvoyée par l'API d'authentification.
type LoginResponse = {
  token: string
  username: string
  roles: string[]
}

export const useAuthStore = defineStore('auth', () => {
  // L'état est persisté localement pour garder la session au rechargement.
  const token = ref(localStorage.getItem('sfe_token') || '')
  const username = ref(localStorage.getItem('sfe_username') || '')
  const loading = ref(false)

  // Un jeton présent suffit ici à considérer l'utilisateur authentifié.
  const isAuthenticated = computed(() => token.value.length > 0)

  // Appel du backend de login puis persistance locale du contexte de session.
  async function login(payload: LoginPayload) {
    loading.value = true
    try {
      const response = await http.post<LoginResponse>('/api/auth/login', payload)
      token.value = response.data.token
      username.value = response.data.username
      localStorage.setItem('sfe_token', token.value)
      localStorage.setItem('sfe_username', username.value)
    } finally {
      loading.value = false
    }
  }

  // Nettoyage de session côté navigateur.
  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('sfe_token')
    localStorage.removeItem('sfe_username')
  }

  return {
    token,
    username,
    loading,
    isAuthenticated,
    login,
    logout,
  }
})