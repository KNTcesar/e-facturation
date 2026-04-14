import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from '@/App.vue'
import { router } from '@/app/router'
import { vuetify } from '@/app/providers/vuetify'
import '@/app/styles/main.scss'
import '@mdi/font/css/materialdesignicons.css'

// Point d'entrée Vue: on attache les plugins globaux avant le montage.
const app = createApp(App)

// Pinia gère l'état partagé, le routeur gère la navigation, Vuetify l'UI.
app.use(createPinia())
app.use(router)
app.use(vuetify)

// Montage final dans la racine HTML.
app.mount('#app')
