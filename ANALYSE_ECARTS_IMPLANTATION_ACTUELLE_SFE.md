# Analyse des écarts de l'implémentation actuelle

## Constat
L'application actuelle n'est pas encore un SFE complet au sens du cahier des charges d'homologation. Elle constitue une base métier solide, mais plusieurs blocs fonctionnels et réglementaires restent absents, partiels ou seulement documentés.

Autrement dit, le projet avance dans la bonne direction, mais il ne faut pas le confondre avec une version prête pour l'homologation DGI.

## Ce qui est réellement en place

### Frontend
- Interface Vue 3 / TypeScript / Vuetify.
- Authentification utilisateur de base.
- Tableau de bord métier.
- Gestion des clients.
- Gestion des produits.
- Configuration du noyau fiscal commun.
- Écrans de facturation.
- Suivi des transmissions SECeF.
- Page d'audit.
- Page de paramètres.
- Mise en page facture avec impression / export visuel.

### Backend
- API Spring Boot.
- Base PostgreSQL.
- Gestion des clients, produits, factures.
- Authentification et sécurité par jeton.
- Numérotation de facture avec cohérence.
- Génération de code d'authentification.
- Génération de payload QR.
- Journal d'audit append-only avec chaînage.
- Suivi des transmissions SECeF.
- Traitement des accusés de réception.

### Ce que cela prouve
Le socle est réel. Le logiciel sait déjà gérer le cycle fiscal de base: identité fiscale, référentiels, facture, transmission, audit et affichage métier.

## Ce qui manque encore dans l'implémentation

### 1. Séquentialité stricte et verrouillage réel de la facture
Le point le plus critique reste la garantie qu'une facture ne peut pas être émise hors séquence.

Il manque encore:
- un verrouillage fort de la séquence d'émission ;
- la preuve qu'il n'existe aucun trou dans la numérotation ;
- la garantie qu'une facture validée ne peut plus être modifiée ;
- une stratégie robuste en cas de plantage au moment de l'émission ;
- des tests de concurrence sur l'émission de facture.

### 2. Immutabilité réglementaire complète
Le journal d'audit existe, mais il faut encore s'assurer que l'immutabilité couvre tous les cas sensibles.

Il manque encore:
- une preuve documentaire de l'immuabilité ;
- la protection complète contre suppression ou édition post-émission ;
- une traçabilité exhaustive des consultations, corrections, rejets et réémissions ;
- une vérification formelle de la chaîne de hachage sur toute la durée de vie.

### 3. Intégration SECeF réellement finalisée
Le suivi SECeF est visible dans l'application, mais cela ne suffit pas pour l'homologation.

Il manque encore:
- la validation explicite du contrat API officiel ;
- des scénarios de rejet complets ;
- la reprise automatique sur échec ;
- la gestion des délais et des files d'attente ;
- les preuves de communication réelles avec le SECeF ;
- les journaux techniques exploitables pour audit et support.

### 4. Dossier d'homologation complet
Le logiciel seul ne suffit pas. Le dossier réglementaire est une partie du livrable.

Il manque encore:
- le tableau de conformité DGI ;
- le manuel administrateur ;
- le manuel utilisateur finalisé ;
- le guide d'installation homologable ;
- les cas de test officiels ;
- la démonstration préparée pour le comité ;
- les annexes administratives et techniques.

### 5. Gestion fine des droits et des profils
Des éléments d'administration existent, mais on ne voit pas encore une matrice de droits très robuste au niveau métier.

Il manque encore:
- une matrice complète des rôles et permissions ;
- un contrôle précis des actions sensibles par rôle ;
- la séparation claire entre administrateur, comptable, caissier, audit, lecture seule ;
- les règles d'accès aux actions d'annulation, certification et réémission.

### 6. Export comptable et fiscal
Le cahier des charges parle d'export SYSCOA / comptable, mais ce n'est pas un bloc clairement finalisé dans l'implémentation visible.

Il manque encore:
- l'export comptable normalisé ;
- les formats exploitables par la comptabilité ;
- les exports fiscaux périodiques ;
- les rapprochements automatiques ;
- les rapports de contrôle fiscal.

### 7. Stock et gestion métier étendue
Le stock est mentionné dans la documentation, mais il n'apparaît pas comme un module finalisé de l'application.

Il manque encore:
- le module stock complet ;
- la gestion des entrées / sorties ;
- les seuils d'alerte ;
- les valorisations ;
- la réservation de stock sur facture ;
- les rapports de mouvement.

### 8. Paiement mobile et encaissement avancé
Il n'y a pas de preuve d'un module de paiement mobile ou d'intégration de wallet dans l'implémentation actuelle.

Il manque encore:
- l'intégration Orange Money / Moov Money / autres moyens locaux ;
- le rapprochement facture / encaissement ;
- les états de paiement ;
- les reçus d'encaissement ;
- la réconciliation comptable.

### 9. Sauvegarde, restauration et reprise
La base technique existe, mais le plan de reprise n'est pas encore livré comme un bloc fonctionnel et documentaire complet.

Il manque encore:
- la politique de sauvegarde ;
- la restauration testée ;
- le plan de reprise après incident ;
- les scénarios de panne ;
- les procédures d'exploitation.

### 10. Tests et validation industrielle
Le projet a des fondations, mais la validation finale reste insuffisante pour une mise en conformité stricte.

Il manque encore:
- des tests d'intégration complets ;
- des tests de charge ;
- des tests de concurrence sur la facture ;
- des tests de régression ;
- des tests d'homologation documentés ;
- des preuves de non-régression après correction.

## Ce qui est probablement encore trop “prototype”
- Les écrans existent, mais certains flux métier sont encore trop orientés démonstration.
- Les textes et documents sont partiellement structurés, mais pas encore livrés au niveau dossier d'homologation.
- La conformité réglementaire est présente dans l'intention, mais pas encore verrouillée par des preuves techniques et des tests.
- La solution couvre le périmètre cœur, mais pas encore tous les modules périphériques attendus dans un produit final.

## Priorités de rattrapage

### Priorité 1 - Bloquants homologation
- séquence inviolable des factures ;
- immutabilité forte ;
- preuve de transmission SECeF ;
- dossier de conformité DGI ;
- tests d'homologation.

### Priorité 2 - Niveau produit
- droits et rôles complets ;
- exports comptables et fiscaux ;
- sauvegarde / reprise ;
- journal d'audit formalisé ;
- documentation utilisateur / admin.

### Priorité 3 - Extension métier
- stock ;
- paiement mobile ;
- reporting avancé ;
- supervision ;
- automatisations.

## Conclusion
Le logiciel actuel a une vraie base fonctionnelle, mais il manque encore plusieurs briques importantes pour correspondre au niveau attendu par le cahier des charges d'homologation. Les plus gros écarts se situent sur la séquentialité, l'immutabilité, la preuve de conformité, les exports réglementaires et le dossier d'homologation.

Le bon diagnostic est donc le suivant: la structure est bonne, mais l'implémentation n'est pas encore complète ni suffisamment verrouillée pour être considérée comme homologable.