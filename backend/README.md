# TogoFood backend — environnement local

Service REST indépendant en Kotlin/Ktor, avec PostgreSQL et migrations Flyway. Ce socle est destiné au développement local ; il ne constitue pas encore un déploiement de production.

## Prérequis

- JDK 17
- Docker Desktop avec Docker Compose
- Le wrapper Gradle à la racine du dépôt

## Démarrer PostgreSQL

Depuis la racine du dépôt :

```powershell
docker compose -f backend/compose.yaml up -d --wait
```

Attends que le service `postgres` soit marqué `healthy` avant de lancer l'API ; sinon Ktor peut démarrer pendant l'initialisation de PostgreSQL et échouer sur sa première connexion.
Le port hôte `5433` évite les conflits avec une installation PostgreSQL locale qui utilise déjà `5432`. Le port dans le conteneur reste `5432`.

Les identifiants définis dans Compose (`togofood`/`togofood`) sont réservés au développement local. Ne pas les réutiliser en production.

## Lancer et tester l'API

```powershell
.\gradlew.bat -p backend test
.\gradlew.bat -p backend run
```

Les tests comptent des cas unitaires et des tests d'intégration PostgreSQL (Testcontainers 2.0, image `postgres:16-alpine`). Sans Docker démarré, les tests d'intégration se **sautent automatiquement** au lieu d'échouer ; démarre Docker Desktop pour les exécuter (aucune variable d'environnement à définir).

Au démarrage, l'API applique les migrations Flyway puis écoute sur `http://localhost:8080`.Si le port `8080` est déjà utilisé, démarre-la sur un autre port :

```powershell
$env:PORT = "8081"
.\gradlew.bat -p backend run
```

```powershell
Invoke-RestMethod http://localhost:8080/health/live
Invoke-RestMethod http://localhost:8080/health/ready
Invoke-RestMethod http://localhost:8080/
Invoke-RestMethod http://localhost:8080/api/v1
```

`/health/live` indique que le processus répond. `/health/ready` répond `200` seulement si PostgreSQL accepte une requête `SELECT 1`, sinon `503`.
Les endpoints métier sont versionnés sous `/api/v1`. Toute erreur ressort au format JSON uniforme `{code, message, requestId, details?}` ; chaque réponse porte un header `X-Request-Id` repris dans les logs.

Documentation interactive : http://localhost:8080/api/v1/docs (Swagger UI). La spécification brute est servie sur http://localhost:8080/api/v1/docs/openapi.yaml et vit dans `src/main/resources/openapi/openapi.yaml`. Swagger UI charge son CSS/JS depuis le CDN `unpkg.com`, donc l'affichage nécessite Internet (l'API, non).

## Configuration

Toutes les valeurs peuvent être surchargées par variables d'environnement :

| Variable | Défaut local | Rôle |
|---|---|---|
| `PORT` | `8080` | Port HTTP |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5433/togofood` | URL JDBC |
| `DATABASE_USER` | `togofood` | Utilisateur PostgreSQL |
| `DATABASE_PASSWORD` | `togofood` | Mot de passe local |
| `DATABASE_POOL_SIZE` | `10` | Taille maximale du pool |

Arrêter PostgreSQL tout en conservant les données locales :

```powershell
docker compose -f backend/compose.yaml down
```

La suppression du volume de base de données n'est pas nécessaire pour le workflow normal. Les migrations appliquées sont conservées dans `flyway_schema_history`.
