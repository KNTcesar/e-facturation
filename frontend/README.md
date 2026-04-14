# Frontend SFE

Le frontend de SFE est l'interface utilisateur de la plateforme d'e-facturation. Il permet de consulter le tableau de bord, gérer les clients et les produits, créer et suivre les factures, piloter les transmissions SECeF, et contrôler la conformité via le journal d'audit.

## Vue d'ensemble

Ce projet a été conçu pour fonctionner avec le backend du dépôt `e-facturation`. Le frontend consomme les API REST du serveur pour afficher les données métier et déclencher les actions principales.

### Ce que l'application permet de faire

- Se connecter à l'application avec une authentification simple.
- Consulter un tableau de bord fiscal avec les indicateurs principaux.
- Gérer les clients et le catalogue de produits.
- Configurer le noyau fiscal commun.
- Créer une facture et suivre son cycle de vie.
- Signer et horodater une facture.
- Suivre les transmissions SECeF, les retries et les accusés de réception.
- Vérifier la chaîne d'audit.
- Personnaliser certains paramètres d'affichage, comme le pied de page.

## Stack technique

- Vue 3
- TypeScript
- Vite
- Pinia pour l'état global
- Vue Router pour la navigation
- Vuetify pour l'interface
- Axios pour les appels API
- SCSS pour les styles globaux

## Structure principale

```text
src/
  app/
    AppShell.vue           # Shell principal de l'application
    router/                # Définition des routes
    providers/             # Configuration des librairies globales
    styles/                # Styles globaux
  widgets/
    navigation/            # Sidebar, topbar et footer
  shared/
    api/                   # Client HTTP partagé
    config/                # Configuration runtime
    ui/                    # Composants UI communs
    utils/                 # Helpers partagés
    mocks/                 # Données de démonstration
  features/
    auth/                  # Authentification
    clients/               # Gestion des clients
    products/              # Gestion des produits
    enterprises/           # Noyau fiscal commun
    invoices/              # Factures et conformité
    transmissions/         # Suivi SECeF
    audit/                 # Journal d'audit
    settings/              # Paramètres applicatifs
  pages/                   # Pages de navigation
```

## Pages importantes

- `DashboardPage.vue` : vue synthétique des indicateurs et des dernières factures.
- `LoginPage.vue` : écran de connexion.
- `ClientsPage.vue` : gestion des clients.
- `ProductsPage.vue` : catalogue des produits.
- `S1NoyauFiscalPage.vue` : configuration du noyau fiscal commun.
- `CreateInvoicePage.vue` : création d'une nouvelle facture.
- `InvoicesPage.vue` : liste, signature, horodatage et export client.
- `TransmissionsPage.vue` : suivi des transmissions SECeF.
- `AuditPage.vue` : vérification du chaînage d'audit.
- `SettingsPage.vue` : préférences et informations d'application.

## Prérequis

- Node.js 18 ou plus récent.
- Un backend SFE accessible via l'URL définie dans `VITE_API_BASE_URL`.

## Installation

Depuis le dossier `frontend` :

```bash
npm install
```

## Configuration

Créer un fichier `.env` dans `frontend/` si besoin :

```env
VITE_API_BASE_URL=http://localhost:8080
```

Si la variable n'est pas définie, le frontend utilisera une base vide et les appels API dépendront de votre configuration locale.

## Lancer le projet

```bash
npm run dev
```

Le frontend sera disponible en général sur `http://localhost:5173`.

## Build de production

```bash
npm run build
```

## Aperçu du fonctionnement

1. L'utilisateur se connecte.
2. Le routeur protège les pages privées.
3. Le store d'authentification conserve le jeton dans le navigateur.
4. Le client HTTP ajoute automatiquement le jeton aux requêtes.
5. Les pages métier consomment les API du backend pour afficher et modifier les données.

## Points à connaître

- Le shell principal affiche la sidebar, la topbar et le footer uniquement quand l'utilisateur est authentifié.
- Les styles globaux sont centralisés dans `src/app/styles/main.scss`.
- Plusieurs écrans s'appuient sur des données formatées côté frontend pour améliorer la lisibilité.
- La page facture contient des fonctions d'export texte, d'impression et de génération de QR code.
- Le noyau fiscal doit être configuré pour que le reste du flux métier soit cohérent.

## Dépannage rapide

- Si l'écran de connexion échoue, vérifier que le backend est lancé.
- Si les données ne s'affichent pas, vérifier la variable `VITE_API_BASE_URL`.
- Si une page privée redirige vers la connexion, vérifier l'état de session dans le navigateur.

## Objectif du projet

Ce frontend sert de cockpit métier pour suivre le cycle fiscal complet: identité fiscale, clients, produits, factures, certification, transmission et audit. Il est pensé pour être une base de travail lisible, structurée et proche du domaine fonctionnel.
