import 'vuetify/styles'
import { createVuetify } from 'vuetify'

// Thème centralisé pour garder la même palette dans toute l'application.
export const vuetify = createVuetify({
  theme: {
    defaultTheme: 'sfeLight',
    themes: {
      sfeLight: {
        dark: false,
        colors: {
          background: '#f6f7fb',
          surface: '#ffffff',
          primary: '#005f73',
          secondary: '#0a9396',
          error: '#ae2012',
          info: '#3a86ff',
          success: '#2a9d8f',
          warning: '#ee9b00',
        },
      },
    },
  },
})