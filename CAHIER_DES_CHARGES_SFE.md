# Cahier des charges

## Système de Facturation d'Entreprise (SFE)
Conforme à la Facture Électronique Certifiée (FEC) - Burkina Faso

**Version :** 1.0  
**Date :** 16 avril 2026

## 1. Présentation du projet

### 1.1 Contexte
Dans le cadre de la modernisation du système fiscal burkinabè, la Direction Générale des Impôts (DGI) a lancé la Facture Électronique Certifiée (FEC) le 6 janvier 2026. Cette réforme impose l'utilisation de systèmes électroniques certifiés pour l'émission des factures et la transmission des données vers la plateforme SECeF.

L'obligation d'utilisation s'applique à partir du 1er juillet 2026 pour les entreprises relevant du Régime Normal d'Imposition (RNI), notamment celles dont le chiffre d'affaires annuel est supérieur ou égal à 50 millions FCFA.

Le présent cahier des charges définit les exigences fonctionnelles, techniques et réglementaires du Système de Facturation d'Entreprise (SFE) à développer pour l'homologation par la DGI.

### 1.2 Objectifs
- Permettre aux entreprises de générer, gérer et transmettre des factures électroniques certifiées en conformité avec la réglementation.
- Garantir la sécurité, l'intégrité, la traçabilité et l'immuabilité des données de facturation.
- Automatiser la transmission des données vers le SECeF.
- Proposer une solution moderne, intuitive et adaptée aux PME burkinabè.
- Obtenir l'attestation d'homologation délivrée par le Comité d'Homologation de la DGI.

## 2. Références réglementaires

Le projet doit respecter les textes et documents suivants :
- Spécifications Techniques des Systèmes de Facturation d'Entreprise (DGI, janvier 2026).
- Procédures d'Homologation des Unités de Facturation, Modules de Contrôle et SFE.
- Arrêté portant conditions et modalités d'émission de la FEC.
- Arrêté portant commercialisation des systèmes électroniques certifiés de facturation.
- Code Général des Impôts.
- Note de la Directrice Générale des Impôts relative aux spécifications techniques des SFE.

## 3. Périmètre fonctionnel

### 3.1 Gestion des données maîtres
Le système doit permettre :
- Le paramétrage complet de l'entreprise : raison sociale, IFU, RCCM, adresse, contacts, régime fiscal, logo, etc.
- La gestion des clients et fournisseurs.
- La gestion du catalogue articles/services : référence, désignation, prix unitaire HT, TVA, unité de mesure, stock optionnel.
- La gestion de séries de facturation multiples.

### 3.2 Cycle de vie des factures
Le système doit permettre :
- La création de différents documents : facture client, facture d'acompte, avoir, facture proforma, etc.
- Le calcul automatique des montants HT, TVA, TTC, remises et frais annexes.
- L'intégration des mentions légales obligatoires et des mentions spécifiques à la FEC.
- La génération automatique d'un code d'authentification unique et sécurisé.
- La génération obligatoire d'un QR Code contenant les informations de vérification conformes aux spécifications DGI.
- La visualisation et l'export au format PDF avec QR Code intégré.
- L'export des données structurées (XML/JSON selon les spécifications DGI).
- L'annulation et la modification avec traçabilité complète dans le journal d'audit.

### 3.3 Sécurité et traçabilité
Les exigences obligatoires pour l'homologation sont :
- Numérotation unique, séquentielle et non modifiable par série.
- Journal d'audit immuable enregistrant toutes les actions : création, modification, annulation, consultation, avec horodatage fiable.
- Impossibilité technique de supprimer ou modifier une facture une fois émise et certifiée.
- Horodatage certifié des opérations.
- Scellement ou signature électronique des factures.
- Protection contre la fraude et les manipulations.

### 3.4 Transmission des données
Le système doit assurer :
- L'envoi des données de facturation vers le SECeF en temps réel ou en mode batch selon les spécifications techniques.
- La gestion des accusés de réception et des statuts : validé / rejeté.
- La génération de rapports de conformité pour les contrôles fiscaux.

### 3.5 Fonctionnalités complémentaires
Le système peut également inclure :
- Un tableau de bord avec statistiques (CA journalier, TVA collectée, nombre de factures, etc.).
- Une recherche avancée et la consultation des factures archivées.
- Un export comptable compatible SYSCOA.
- La gestion des utilisateurs et des droits d'accès (administrateur, comptable, caissier, etc.).
- Une sauvegarde automatique et sécurisée des données.
- La mise à jour automatique des paramètres fiscaux.

## 4. Exigences techniques et non fonctionnelles

### 4.1 Architecture
- Solution web SaaS recommandée.
- Version installable en local (on-premise) pour les entreprises à faible connectivité.

### 4.2 Technologies
La stack technique est à définir, mais doit rester compatible avec les besoins du projet. À titre indicatif :
- Backend : Laravel, Django ou Node.js.
- Frontend : React ou Vue.js.
- Base de données : PostgreSQL recommandée, notamment pour les logs d'audit.

### 4.3 Sécurité
Le système doit intégrer :
- HTTPS.
- Authentification forte.
- Chiffrement des données sensibles.
- Logs immuables.
- Respect strict des exigences anti-fraude de la DGI.

### 4.4 Performance
Le système doit pouvoir traiter plusieurs milliers de factures par jour.

### 4.5 Interopérabilité
Le système doit pouvoir se connecter à l'API SECeF de la DGI conformément aux spécifications techniques publiées.

### 4.6 Ergonomie
- Interface intuitive.
- Design responsive et compatible mobile.
- Interface en français.

### 4.7 Maintenance
Le système doit permettre :
- Des mises à jour automatiques.
- L'évolution des règles fiscales et des paramètres réglementaires.

## 5. Livrables attendus

Le projet doit fournir :
- Le logiciel complet et fonctionnel.
- Un manuel utilisateur détaillé.
- Un guide d'installation et d'administration.
- Une documentation technique complète (architecture, API, base de données).
- Les fichiers d'installation pour la version locale.
- Les cas de test et un tableau de conformité aux spécifications DGI.
- Un support de démonstration pour le Comité d'Homologation.

## 6. Phases du projet et planning indicatif

- Analyse et conception détaillée : 2 à 3 semaines.
- Développement du noyau (facturation + sécurité + QR Code) : 8 à 12 semaines.
- Intégration de la transmission SECeF et tests unitaires : 4 semaines.
- Tests complets et recette interne : 3 semaines.
- Préparation et dépôt du dossier d'homologation DGI : 2 semaines.
- Corrections post-homologation et déploiement commercial : selon retour DGI.

**Objectif global :** version homologable avant fin juin 2026.

## 7. Homologation DGI
Le logiciel devra respecter intégralement les spécifications techniques des SFE. Après développement, le dossier complet devra être constitué puis déposé auprès de la DGI.

Le dossier d'homologation doit comprendre notamment :
- le formulaire de demande,
- les documents administratifs,
- les spécifications techniques,
- les manuels utilisateur et administrateur,
- la démonstration du logiciel,
- les cas de test,
- le tableau de conformité.

Les dossiers sont recevables depuis le 15 avril 2026.

## 8. Critères de réussite

Le projet sera considéré comme réussi si :
- les factures sont générées conformément aux exigences FEC,
- les transmissions SECeF fonctionnent correctement,
- le journal d'audit est complet et immuable,
- le QR Code et le code d'authentification sont conformes,
- le logiciel est homologable par la DGI,
- le système est utilisable en production par les entreprises ciblées.

## 9. Prochaines étapes recommandées

- Valider ou ajuster ce cahier des charges.
- Choisir la stack technique définitive.
- Rédiger les spécifications fonctionnelles détaillées par écran.
- Définir le schéma de base de données.
- Détailler l'algorithme du QR Code et du code d'authentification.
- Préparer le tableau de conformité aux spécifications DGI.
