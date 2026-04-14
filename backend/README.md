# Backend SFE

Le backend SFE est le socle API de la plateforme d'e-facturation. Il expose les services métier (authentification, clients, produits, factures, transmissions, audit), applique les règles de sécurité, et persiste les données dans PostgreSQL.

## Objectif

Fournir une API REST robuste pour piloter le cycle fiscal:

- authentification des utilisateurs
- gestion des référentiels (clients, produits)
- création et suivi des factures
- préparation et suivi des transmissions SECeF
- traçabilité via journal d'audit

## Stack technique

- Java 21
- Spring Boot 3.3
- Spring Security + JWT
- Spring Data JPA
- Flyway (migrations de schéma)
- PostgreSQL
- OpenAPI / Swagger

## Organisation du code

```text
src/main/java/bf/gov/dgi/sfe
├── config/        # Configuration Spring, sécurité, CORS, etc.
├── controller/    # Endpoints REST
├── dto/           # Objets d'entrée/sortie de l'API
├── entity/        # Entités JPA
├── enums/         # Enumérations métier
├── exception/     # Gestion centralisée des erreurs
├── repository/    # Accès aux données
├── security/      # JWT, filtres, user principal
├── service/       # Logique métier
└── bootstrap/     # Données d'initialisation
```

## Lecture rapide des couches

- `controller`: reçoit les requêtes HTTP et renvoie les réponses JSON.
- `service`: applique la logique métier et orchestre les opérations.
- `repository`: lit/écrit en base via JPA.
- `entity`: représente le modèle persisté.
- `dto`: définit les contrats API frontend/backend.
- `security`: gère login, token JWT et autorisations.
- `exception`: standardise les erreurs métier/techniques.

## Prérequis

- Java 21 installé
- Maven 3.9+
- PostgreSQL disponible
- Docker Desktop (optionnel, recommandé)

## Démarrage rapide (Docker)

Depuis la racine du projet:

```bash
docker compose up --build
```

Services accessibles:

- API backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Mode détaché:

```bash
docker compose up --build -d
```

Arrêt:

```bash
docker compose down
```

## Démarrage local (sans Docker)

Depuis `backend/`:

```bash
mvn spring-boot:run
```

Avant de lancer en local, vérifier la configuration base de données et JWT dans `src/main/resources/application.yml`.

## Configuration

Le fichier principal est:

- `src/main/resources/application.yml`

Points importants:

- `spring.datasource.*` pour la connexion PostgreSQL
- `spring.jpa.*` pour JPA/Hibernate
- `spring.flyway.*` pour les migrations
- `app.jwt.secret` pour signer les tokens JWT

En mode Docker, une partie de ces valeurs peut être injectée via `docker-compose.yml`.

## Authentification

Endpoint:

- `POST /api/auth/login`

Identifiants de démonstration (bootstrap):

- `admin` / `admin123`

Exemple de requête:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Le token retourné doit être envoyé dans l'en-tête HTTP:

```text
Authorization: Bearer <token>
```

## Endpoints principaux

### Auth

- `POST /api/auth/login`

### Clients

- `GET /api/clients`
- `POST /api/clients`

### Produits

- `GET /api/produits`
- `POST /api/produits`

### Factures

- `POST /api/factures`
- `GET /api/factures`
- `GET /api/factures/{id}`
- `POST /api/factures/{id}/annuler`
- `POST /api/factures/{id}/signer`
- `GET /api/factures/{id}/signature`
- `POST /api/factures/{id}/horodater`
- `GET /api/factures/{id}/horodatage`

### Transmissions SECeF

- `GET /api/transmissions-secef`
- `GET /api/transmissions-secef/{id}`
- `POST /api/transmissions-secef/dispatch/pending`
- `POST /api/transmissions-secef/{id}/dispatch`
- `POST /api/transmissions-secef/{id}/ack`

### Audit

- `GET /api/journal-audit`
- `GET /api/journal-audit/verify`

## Documentation API

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Migrations de base de données

Les migrations sont versionnées avec Flyway dans:

- `src/main/resources/db/migration`

Elles sont appliquées automatiquement au démarrage (selon configuration).

## Fonctionnalités déjà en place

- numérotation des factures avec mécanisme de cohérence
- génération de code d'authentification
- génération de payload QR
- initialisation de transmission SECeF
- dispatch manuel/planifié avec logique de retry
- traitement des accusés de réception (accepté / rejeté)
- journal d'audit append-only avec vérification

## Conseils de prise en main

1. Démarrer la stack avec Docker.
2. Tester la connexion via `POST /api/auth/login` dans Swagger.
3. Créer quelques clients et produits.
4. Émettre une facture puis vérifier les flux de transmission.
5. Contrôler la chaîne d'audit.

## Pistes d'amélioration

- renforcer la couverture de tests unitaires et d'intégration
- intégrer des tests de persistance avec Testcontainers
- brancher un connecteur SECeF réel en production
- améliorer observabilité (logs structurés, métriques, traces)
- documenter les règles métier avancées par domaine
