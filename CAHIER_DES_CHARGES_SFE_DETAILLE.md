# Cahier des charges détaillé

## Système de Facturation d'Entreprise (SFE)
Conforme à la Facture Électronique Certifiée (FEC) - Burkina Faso

**Version :** 1.0  
**Date :** 16 avril 2026  
**Objet :** Spécifications fonctionnelles, techniques et réglementaires pour le développement d'un Système de Facturation d'Entreprise homologable par la DGI.

---

## 1. Contexte général

### 1.1 Contexte réglementaire
Dans le cadre de la modernisation du système fiscal burkinabè, la Direction Générale des Impôts (DGI) a institué la Facture Électronique Certifiée (FEC). Cette réforme impose aux entreprises concernées l'utilisation de solutions de facturation électroniques certifiées, capables de produire des factures conformes, de sécuriser les données et de transmettre les informations requises vers la plateforme SECeF.

L'obligation de conformité vise en priorité les entreprises relevant du Régime Normal d'Imposition (RNI), notamment celles dont le chiffre d'affaires annuel atteint ou dépasse 50 millions FCFA.

### 1.2 Finalité du projet
Le présent projet vise à concevoir, développer, tester et préparer à l'homologation un Système de Facturation d'Entreprise (SFE) répondant aux exigences réglementaires de la DGI et aux besoins opérationnels des entreprises burkinabè.

### 1.3 Objectifs opérationnels
Le système doit permettre de :
- créer et gérer des factures électroniques conformes ;
- garantir l'intégrité, l'immutabilité et la traçabilité des opérations ;
- automatiser les calculs fiscaux et les contrôles de cohérence ;
- intégrer le QR Code et le code d'authentification ;
- transmettre les données de facturation au SECeF ;
- faciliter le contrôle interne, l'audit et la préparation du dossier d'homologation.

---

## 2. Périmètre fonctionnel

### 2.1 Gestion des données de base
Le logiciel doit prendre en charge les référentiels suivants :

#### 2.1.1 Entreprise
Le paramétrage de l'entreprise doit permettre de renseigner :
- raison sociale ;
- sigle commercial ;
- IFU ;
- RCCM ;
- adresse géographique et postale ;
- ville, pays et localisation ;
- numéro de téléphone ;
- adresse email ;
- logo de l'entreprise ;
- régime fiscal ;
- informations d'immatriculation ;
- contacts administratifs et techniques.

#### 2.1.2 Clients
La gestion client doit inclure :
- création, modification, consultation et archivage ;
- identification complète du client ;
- IFU éventuel ;
- RCCM éventuel ;
- adresse et contacts ;
- historique des factures ;
- recherche et filtrage avancés.

#### 2.1.3 Fournisseurs
La solution doit prévoir un référentiel fournisseurs avec :
- identité juridique ;
- coordonnées ;
- catégorisation ;
- historique des transactions ;
- archivage des documents liés.

#### 2.1.4 Produits et services
Le catalogue doit contenir au minimum :
- référence article/service ;
- désignation ;
- description ;
- unité de mesure ;
- prix unitaire HT ;
- taux de TVA ;
- nature taxable ou exonérée ;
- stock optionnel ;
- statut actif/inactif.

#### 2.1.5 Séries de facturation
Le système doit gérer :
- plusieurs séries de numérotation ;
- une séquence unique par série ;
- un verrouillage des séquences ;
- une traçabilité complète des ruptures et anomalies.

### 2.2 Cycle de vie des documents de facturation
Le système doit prendre en charge les documents suivants :
- facture standard ;
- facture d'acompte ;
- avoir ;
- facture proforma ;
- facture d'annulation ;
- documents internes de correction ou de régularisation.

Pour chaque document, la solution doit permettre :
- la saisie des lignes de facturation ;
- le calcul automatique HT, TVA, TTC, remises et frais annexes ;
- l'application des règles fiscales ;
- la génération de numéros séquentiels ;
- la validation avant certification ;
- l'export PDF ;
- la consultation et l'archivage.

### 2.3 Mentions obligatoires
Le document émis doit intégrer toutes les mentions légales et fiscales obligatoires, notamment :
- identité de l'émetteur ;
- identité du client ;
- date et numéro de facture ;
- description des biens ou services ;
- montants HT, TVA, TTC ;
- taux de taxation ;
- code d'authentification ;
- QR Code ;
- mention de conformité FEC ;
- statut de transmission le cas échéant.

### 2.4 Code d'authentification et QR Code
Le système doit :
- générer un code d'authentification unique pour chaque facture ;
- produire un QR Code conforme aux spécifications de la DGI ;
- garantir l'unicité, la vérifiabilité et la résistance à la falsification ;
- permettre la lecture du QR Code pour vérification par les services compétents.

### 2.5 Journal d'audit
Le journal d'audit doit enregistrer toute opération sensible :
- création ;
- modification ;
- annulation ;
- consultation ;
- transmission ;
- rejet ;
- validation ;
- action d'administration.

Chaque événement d'audit doit contenir au minimum :
- date et heure exactes ;
- utilisateur concerné ;
- nature de l'action ;
- objet impacté ;
- identifiant technique ;
- résultat de l'opération ;
- référence d'intégrité ou de chaînage.

### 2.6 Transmission SECeF
La solution doit pouvoir :
- envoyer les données vers le SECeF en temps réel ou en mode batch ;
- gérer les accusés de réception ;
- enregistrer les statuts : accepté, rejeté, en attente, en échec ;
- permettre les tentatives automatiques de reprise ;
- produire des journaux de transmission exploitables pour le support et l'audit.

### 2.7 Consultation, recherche et reporting
Le système doit offrir :
- recherche multicritère des factures ;
- filtres par client, période, statut, série, montant, utilisateur ;
- tableaux de bord de pilotage ;
- statistiques de chiffre d'affaires ;
- TVA collectée ;
- volume de factures ;
- taux d'acceptation des transmissions ;
- exports comptables et fiscaux.

---

## 3. Exigences fonctionnelles détaillées

### 3.1 Gestion des utilisateurs
Le système doit supporter plusieurs profils :
- administrateur système ;
- administrateur métier ;
- comptable ;
- caissier ;
- auditeur ;
- utilisateur en lecture seule.

Pour chaque rôle, il faut définir :
- droits d'accès ;
- périmètre de consultation ;
- actions autorisées ;
- journalisation des activités.

### 3.2 Gestion des accès et sécurité applicative
Le logiciel doit mettre en œuvre :
- authentification sécurisée ;
- gestion de session ;
- protection contre les attaques courantes ;
- politique de mot de passe robuste ;
- verrouillage ou limitation des tentatives de connexion ;
- audit des connexions et déconnexions.

### 3.3 Contrôles de cohérence métier
Le système doit vérifier :
- l'existence du client avant émission ;
- la cohérence des taux de TVA ;
- l'unicité du numéro de facture ;
- la validité des montants ;
- l'équilibre HT/TVA/TTC ;
- la conformité des numéros de série ;
- la présence des champs obligatoires.

### 3.4 Archivage
Le système doit assurer :
- l'archivage des factures émises ;
- la conservation des traces d'audit ;
- la réversibilité et l'export des données ;
- la consultation historique ;
- la rétention selon les exigences légales et fiscales.

---

## 4. Exigences non fonctionnelles

### 4.1 Architecture cible
Le système doit être conçu selon une architecture :
- web moderne ;
- sécurisée ;
- évolutive ;
- compatible SaaS et installation locale ;
- modulaire pour faciliter la maintenance.

### 4.2 Technologies recommandées
La stack technique peut reposer sur :
- Backend : Spring Boot, Laravel, Django ou Node.js ;
- Frontend : Vue.js ou React ;
- Base de données : PostgreSQL recommandée ;
- Authentification : JWT ou mécanisme équivalent ;
- PDF et QR Code : génération côté serveur ou côté client selon la conception.

### 4.3 Sécurité
Les exigences minimales sont :
- HTTPS obligatoire ;
- chiffrement des données sensibles ;
- protection contre les injections et falsifications ;
- journalisation immuable ;
- séparation des privilèges ;
- traçabilité des accès ;
- sécurisation des clés et secrets applicatifs.

### 4.4 Performance
Le système doit :
- supporter plusieurs milliers de factures par jour ;
- répondre rapidement aux principales opérations ;
- assurer une consultation fluide des données historiques ;
- rester stable en cas de charge élevée.

### 4.5 Disponibilité et fiabilité
Le système doit prévoir :
- sauvegardes régulières ;
- reprise sur incident ;
- restauration des données ;
- mécanismes de prévention de corruption ;
- supervision de l'état des services.

### 4.6 Interopérabilité
La solution doit être capable de :
- intégrer l'API SECeF ;
- produire les formats attendus par la DGI ;
- s'adapter aux évolutions des spécifications techniques ;
- exposer des données exploitables pour le contrôle et la conformité.

### 4.7 Ergonomie
L'interface doit :
- être intuitive ;
- être responsive ;
- rester utilisable sur ordinateur et mobile ;
- être entièrement en français ;
- proposer des écrans clairs et cohérents ;
- limiter la complexité des opérations usuelles.

---

## 5. Exigences techniques de conformité DGI

Le système doit respecter notamment :
- la numérotation séquentielle et non altérable ;
- la génération du code d'authentification ;
- l'intégrité des données ;
- l'horodatage fiable ;
- la conservation des traces d'émission ;
- la transmission des données exigées ;
- la disponibilité des preuves de conformité ;
- la capacité de démonstration devant le comité d'homologation.

---

## 6. Livrables attendus

Le projet devra fournir :
- le logiciel complet et fonctionnel ;
- la documentation fonctionnelle ;
- la documentation technique ;
- le manuel utilisateur ;
- le guide d'installation et d'administration ;
- le schéma de base de données ;
- les spécifications API ;
- les cas de test ;
- le tableau de conformité DGI ;
- le support de démonstration pour l'homologation.

---

## 7. Organisation projet et planning indicatif

### 7.1 Phases
1. Analyse détaillée et cadrage.
2. Conception fonctionnelle et technique.
3. Développement du noyau applicatif.
4. Intégration QR Code, authentification et audit.
5. Intégration transmission SECeF.
6. Tests unitaires, fonctionnels et d'intégration.
7. Préparation du dossier d'homologation.
8. Ajustements post-retour DGI.

### 7.2 Estimation
- Analyse et conception : 2 à 3 semaines.
- Développement du noyau : 8 à 12 semaines.
- Intégration SECeF et tests : 4 semaines.
- Recette interne : 3 semaines.
- Dossier d'homologation : 2 semaines.

**Objectif global :** disposer d'une version homologable avant fin juin 2026.

---

## 8. Critères d'acceptation

Le projet sera jugé conforme si :
- les factures sont produites selon les règles FEC ;
- les codes d'authentification sont uniques et vérifiables ;
- le QR Code est conforme ;
- le journal d'audit est complet et non altérable ;
- les transmissions SECeF sont fonctionnelles ;
- les profils utilisateurs et les droits sont correctement appliqués ;
- les livrables d'homologation sont complets ;
- le logiciel peut être présenté à la DGI pour validation.

---

## 9. Annexes et suites à produire

À partir de ce cahier des charges, les documents suivants peuvent être rédigés :
- spécifications fonctionnelles détaillées par écran ;
- spécifications techniques d'architecture ;
- modèle de données ;
- règles de génération du code d'authentification ;
- structure du QR Code ;
- tableau de conformité aux exigences DGI ;
- plan de tests d'homologation.
