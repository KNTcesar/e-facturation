# Analyse des écarts entre le cahier des charges et l'application actuelle

## Lecture rapide
Le cahier des charges décrit une cible d'homologation complète. L'application actuelle couvre une base fonctionnelle utile, mais elle ne couvre pas encore l'ensemble des exigences réglementaires, documentaires et techniques attendues pour l'homologation DGI.

En pratique, le projet n'est pas "hors sujet". Il est plutôt dans une phase intermédiaire : base métier en place, mais niveau de conformité et de formalisation encore insuffisant.

## Ce qui est déjà en place
- Frontend Vue 3 + TypeScript + Vuetify.
- Backend Spring Boot + PostgreSQL.
- Gestion des clients, produits, factures.
- Noyau fiscal commun / profil entreprise.
- Tableau de bord.
- Journal d'audit visible côté application.
- Suivi des transmissions SECeF dans l'interface.
- Génération d'éléments d'impression / export facture.
- Structure projet cohérente et exploitable.

## Ce qui est partiellement couvert
- Authentification et gestion des rôles : la base existe, mais le niveau de détail métier / sécurité peut encore être renforcé.
- Audit : présent, mais il faut encore vérifier que toutes les exigences d'immuabilité et de chaîne de preuve sont bien appliquées à tous les cas.
- Facturation : les écrans et flux existent, mais les contraintes DGI les plus strictes doivent encore être contrôlées.
- QR Code et code d'authentification : présents dans la logique projet, mais à valider précisément par rapport aux spécifications officielles.
- Transmission SECeF : le suivi métier est là, mais l'intégration réelle avec le système externe doit être confirmée et durcie.
- PDF / export : fonctionnel, mais le format final homologable peut demander des ajustements.

## Ce qui manque encore pour viser l'homologation
- Dossier d'homologation complet et structuré.
- Tableau formel de conformité DGI.
- Spécifications fonctionnelles détaillées par écran et par cas d'usage.
- Modèle de données final validé.
- Séquentialité stricte de délivrance des factures avec verrouillage de la numérotation.
- Vérification de l'immutabilité forte des factures et de l'audit.
- Gestion exhaustive des droits par profil métier.
- Sauvegarde, restauration et plan de reprise documentés.
- Export SYSCOA / comptable si exigé.
- Tests d'homologation complets et documentés.
- Validation de l'intégration SECeF réelle selon les API officielles.

## Conclusion
Le logiciel actuel sert de bonne base, mais il n'est pas encore au niveau du cahier des charges d'homologation. Il faut maintenant le transformer en produit réglementaire complet : verrouillage des règles métier, preuves de conformité, documentation, tests, et intégration officielle SECeF.

Un point particulièrement critique est la délivrance séquentielle des factures : si la numérotation ou l'émission n'est pas strictement contrôlée, cela devient bloquant pour l'homologation.

## Prochaine étape recommandée
Produire un tableau à trois colonnes :
- Déjà fait
- Partiellement fait
- Manquant pour l'homologation

C'est le document le plus utile pour décider quoi développer en priorité.
