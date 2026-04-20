# Analyse des écarts backend vs documentation DGI

## Résumé exécutif
Le backend actuel couvre le socle (auth, clients, produits, factures, audit, transmissions), mais il ne couvre pas encore une grande partie des exigences normatives détaillées (types de facture officiels, taxations avancées, rapports X/Z/A, MCF, commentaires paramétrés, paiements détaillés, inventaire, etc.).

Niveau actuel estimé (backend):
- Socle métier: bon
- Conformité réglementaire détaillée: insuffisante
- Homologation DGI: non prête

## Lecture par statut
- Couvre: exigence implémentée de manière visible côté backend
- Partiel: début d'implémentation, mais incomplet pour la norme
- Manquant: absent du backend actuel

## Exigences générales (section 2)

### 2.1 à 2.4 (interfaces, ISF, sécurité MCF, cohérence données imprimées)
- Statut: Partiel / Manquant
- Observations:
  - ISF non visible comme identifiant métier géré dans les entités principales.
  - Communication formelle MCF non implémentée (pas de connecteur/protocole MCF).
  - Contrôle "données facture imprimées == données envoyées MCF" non présent.

### 2.6 (pas de facture sans identification des articles)
- Statut: Couvre
- Observations:
  - Facture créée avec des lignes obligatoires liées à des produits.

### 2.7 à 2.10 (types de factures officiels FV/FT/FA/EV/ET/EA)
- Statut: Manquant
- Observations:
  - Aucun enum ou champ typeFacture officiel visible dans Facture.
  - Pas de règles métier différenciées par type officiel.

### 2.11 (duplicata)
- Statut: Manquant
- Observations:
  - Pas d'API dédiée duplicata et pas de flag/mention duplicata structuré.

### 2.12 (rapports X, Z, A)
- Statut: Manquant
- Observations:
  - Aucun service/controller de génération de rapports réglementaires X/Z/A.

### 2.13 (dépôts/retraits numéraires)
- Statut: Manquant
- Observations:
  - Aucun module caisse/numéraire côté backend.

### 2.14 (type client + champs obligatoires selon type)
- Statut: Manquant
- Observations:
  - Client actuel: nom, ifu, adresse, téléphone, email.
  - Pas de type client CC/PM/PP/PC ni validation conditionnelle.

### 2.15 (16 groupes de taxation)
- Statut: Manquant
- Observations:
  - Produit n'expose qu'un taux TVA simple.
  - Pas de groupe taxation A..P ni logique associée.

### 2.16 (4 groupes PSVB)
- Statut: Manquant
- Observations:
  - Aucun modèle PSVB ni calcul PSVB par groupe.

### 2.17 (taxe spécifique article)
- Statut: Manquant
- Observations:
  - Pas de champ taxe spécifique sur produit/ligne facture.

### 2.18 (référence unique ininterrompue annuelle)
- Statut: Partiel
- Observations:
  - Série + exercice + prochain_numero existent.
  - Besoin de preuves anti-trou fortes (crash/concurrence/rollback) et de scénarios extrêmes testés.

### 2.19 (gestion article enrichie: type article, groupe taxation, taxe spécifique...)
- Statut: Manquant
- Observations:
  - Produit actuel trop minimal pour la norme.

### 2.20 (inventaire stock)
- Statut: Manquant
- Observations:
  - Aucun module stock/mouvements/inventaire.

### 2.21 (modes de paiement: virement/carte/mobile/chèque/espèces/crédit)
- Statut: Manquant
- Observations:
  - Aucun modèle de ventilation multi-modes sur facture.

### 2.22 (somme des paiements = total facture)
- Statut: Manquant
- Observations:
  - Non applicable tant que 2.21 absent.

### 2.23 (journal électronique factures + rapports + code SECeF/DGI)
- Statut: Partiel
- Observations:
  - Journal d'audit chaîné présent.
  - Mais pas un "journal électronique réglementaire" intégrant aussi les rapports X/Z/A.

### 2.24 et 2.25 (interdiction montants nuls/négatifs)
- Statut: Partiel
- Observations:
  - Contrôles explicites non visibles partout (ligne, facture, calculs).

### 2.26 (configuration port MCF)
- Statut: Manquant
- Observations:
  - Pas de configuration MCF dans le backend.

### 2.27 (8 lignes de commentaires paramétrées)
- Statut: Manquant
- Observations:
  - Pas de structure "commentaire facture" A..H.

### 2.28 et 2.29 (nature d'avoir + cas RRR)
- Statut: Manquant
- Observations:
  - Annulation existe, mais logique complète d'avoir réglementaire absente.

### 2.30 (conformité protocole MCF + QR scannable selon norme)
- Statut: Partiel / Manquant
- Observations:
  - QR payload généré.
  - Protocole MCF officiel non implémenté.

### 2.31 (alerte MCF non connecté > 7 jours)
- Statut: Manquant
- Observations:
  - Dépend d'un vrai connecteur MCF.

### 2.32 à 2.35 (comptes bancaires, régime, service impôts, tableaux paramétrage)
- Statut: Partiel
- Observations:
  - Une partie noyau fiscal existe (entreprise/régime) mais pas l'ensemble des tableaux paramétrés officiels.

## Spécifications facture (section 3)
- Statut global: Partiel
- Couvert:
  - nom/IFU entreprise, client, totaux, date, numéro, code auth, QR, statut.
- Manquant/partiel:
  - mentions réglementaires complètes par type de facture,
  - montant en lettres,
  - timbre quittance espèce,
  - modes de paiement structurés,
  - type client affiché selon table,
  - éléments MCF/SECeF normés,
  - format cadastral et champs normatifs complets.

## Rapports réglementaires (sections 4 et 5)
- Statut: Manquant
- Observations:
  - Pas de génération X, Z, A rapports avec contenu imposé.

## Calculs (section 6)
- Statut global: Partiel
- Couvert:
  - calcul HT/TVA/TTC basique.
- Manquant:
  - mode prix HT/TTC paramétrable par facture,
  - arrondis normés détaillés,
  - taxe spécifique,
  - PSVB,
  - calculs par groupes de taxation complets.

## Points déjà solides à conserver
- Base Spring Boot + PostgreSQL propre.
- Flux création facture + transmission + audit déjà présents.
- Numérotation structurée par série/exercice.
- Chaînage audit et endpoints de vérification.

## Feuille de route backend (ordre recommandé)

### Lot 1 - Blocage conformité facture
1. Ajouter typeFacture (FV/FT/FA/EV/ET/EA) + validations métier.
2. Ajouter natureFactureAvoir (COR/RAN/RAM/IRRR).
3. Ajouter typeClient (CC/PM/PP/PC) + validations conditionnelles.
4. Ajouter modes de paiement ventilés + règle somme = total.
5. Ajouter commentaires facture A..H paramétrables.

### Lot 2 - Taxation et calculs normatifs
1. Introduire groupes de taxation A..P.
2. Introduire groupes PSVB + calcul PSVB.
3. Introduire taxe spécifique article/ligne.
4. Introduire mode prix HT/TTC par facture.
5. Harmoniser règles d'arrondi normatives.

### Lot 3 - Rapports réglementaires
1. Générer Z-rapport.
2. Générer X-rapport quotidien/périodique.
3. Générer A-rapport article.
4. Journal électronique consolidé (factures + rapports + codes sécurité).

### Lot 4 - MCF/SECeF conformité technique
1. Remplacer le stub SECeF par un connecteur contractuel.
2. Ajouter couche protocole MCF (ou adaptateur certifiable).
3. Ajouter contrôles et alertes MCF non connecté > 7 jours.
4. Ajouter preuves techniques/traçabilité homologation.

### Lot 5 - Robustesse homologation
1. Tests de concurrence numérotation (anti-trou/anti-doublon).
2. Tests de non-régression calculs fiscaux.
3. Tests d'intégration réglementaires.
4. Dossier preuves techniques backend.

## Verdict backend
Le backend est une bonne base de travail mais il est encore loin du niveau de couverture exigé par la documentation réglementaire complète.
La priorité immédiate est d'implémenter les exigences de la section 2 (types, taxations, paiements, commentaires) avant de viser l'homologation.
