import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/features/auth/store/useAuthStore'

// Déclaration centralisée des routes frontend.
const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/pages/LoginPage.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('@/pages/DashboardPage.vue'),
  },
  {
    path: '/clients',
    name: 'clients',
    component: () => import('@/pages/ClientsPage.vue'),
  },
  {
    path: '/produits',
    name: 'produits',
    component: () => import('@/pages/ProductsPage.vue'),
  },
  {
    path: '/noyau-fiscal',
    name: 'noyau-fiscal',
    component: () => import('@/pages/S1NoyauFiscalPage.vue'),
  },
  {
    path: '/factures',
    name: 'factures',
    component: () => import('@/pages/InvoicesPage.vue'),
  },
  {
    path: '/factures/creer',
    name: 'factures-create',
    component: () => import('@/pages/CreateInvoicePage.vue'),
  },
  {
    path: '/transmissions',
    name: 'transmissions',
    component: () => import('@/pages/TransmissionsPage.vue'),
  },
  {
    path: '/audit',
    name: 'audit',
    component: () => import('@/pages/AuditPage.vue'),
  },
  {
    path: '/parametres',
    name: 'parametres',
    component: () => import('@/pages/SettingsPage.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/pages/NotFoundPage.vue'),
    meta: { public: true },
  },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Garde globale: protège les pages privées et évite de renvoyer un utilisateur connecté vers /login.
router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.public) {
    if (to.name === 'login' && authStore.isAuthenticated) {
      return { name: 'dashboard' }
    }
    return true
  }

  if (!authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  return true
})