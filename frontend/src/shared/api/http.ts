import axios from 'axios'
import { env } from '@/shared/config/env'

// Client HTTP partagé: base URL, timeout et injection automatique du jeton.
export const http = axios.create({
  baseURL: env.apiBaseUrl,
  timeout: 20000,
})

// Ajoute le JWT dans chaque requête si l'utilisateur est déjà connecté.
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('sfe_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})