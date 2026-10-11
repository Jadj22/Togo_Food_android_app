# TogoFood backend — Suivi E0 (état et explications)

**Dernière mise à jour :** 11 octobre 2026 — étapes 1 à 6 livrées, tests verts (23/23 avec Docker démarré)  
**Objectif du fichier :** resituer le contexte et l'avancement avant de reprendre à l'étape suivante.

## Où on en est

```text
✅ Fait (socle M0) :
   - Serveur Ktor qui démarre
   - PostgreSQL en Docker + migrations Flyway (1ère table : zones)
   - Points de santé /health/live et /health/ready
   - Tests existants verts

🔨 En cours (E0 — étapes) :
   [x] Étape 1 — Format d'erreur uniforme + requestId        ← livrée
   [x] Étape 2 — Prefix /api/v1 + requestId dans les logs    ← livrée
   [x] Étape 3 — Validation des query params                 ← livrée
   [x] Étape 4 — OpenAPI + Swagger UI                          ← livrée (spec statique ; code-first différé, cf. ci-dessous)
   [x] Étape 5 — Tests d'intégration PostgreSQL (Testcontainers)  ← livrée (3 tests verts, Docker requis)
   [x] Étape 6 — CI GitHub Actions                              ← écrite (à valider par un push)

⬜ Ensuite :
   - E1 : endpoints métier publics (zones, catégories, vendeurs, préparations)
   - E2 → E6 : identité, écritures vendeur, favoris, avis, durcissement
```

Références : `docs/backend/PLAN.md` (vision), `docs/backend/ENDPOINTS-IMPLEMENTATION-PLAN.md` (plan de livraison, décisions D1–D4).

## Étape 1 — ce qui a été livré

### 1. Format d'erreur unique (plugin StatusPages)

Toute erreur ressort dans le même format JSON :

```json
{
  "code": "NOT_FOUND",
  "message": "Zone abc introuvable.",
  "requestId": "3f2a-…",
  "details": [{ "field": "zoneId", "reason": "invalid_format" }]
}
```

- `StatusPages` intercepte toutes les erreurs avant qu'elles n'atteignent le client et les convertit en ce format.
- Les routes n'écrivent plus les erreurs à la main : elles `throw` une exception métier, le plugin traduit.
- `code` est stable : l'app Android s'appuie dessus pour localiser le message, pas sur le texte.

### 2. `requestId` (plugin CallId)

Chaque requête reçoit un numéro de dossier :
- généré automatiquement, ou repris du header entrant `X-Request-Id` ;
- renvoyé dans la réponse (header + corps d'erreur) ;
- utilisé dans les logs quand une exception inattendue se produit.

Utilité : un utilisateur signale « j'ai une erreur 500 » → son `requestId` permet de retrouver l'exception exacte dans les logs, sans jamais exposer les détails techniques (stacktrace, identifiants) au client. Un test vérifie qu'aucun détail sensible n'apparaît dans la réponse.

### 3. Exceptions métier

```kotlin
NotFoundException("Zone", "abc")  → 404  NOT_FOUND
ValidationException(…)            → 400  VALIDATION_ERROR + details
ConflictException(…)              → 409  CONFLICT
RateLimitedException(…)           → 429  RATE_LIMITED        (activé en E2)
toute autre exception             → 500  INTERNAL_ERROR      (détails masqués)
```

### 4. Tests (6 ajoutés)

Ils verrouillent le contrat : un client Android doit toujours pouvoir lire `code` + `requestId`. Si le format casse, les tests échouent.

- 404 route inconnue → payload JSON + `requestId` cohérent avec le header
- 404 métier → message de l'exception préservé
- 409 conflit, 400 validation avec `details`
- 500 → aucun détail fuité
- `X-Request-Id` entrant réutilisé dans header + corps

## Étape 2 — ce qui a été livré

### Prefix `/api/v1`

- `common/api/ApiRoutes.kt` : `ServiceInfo` (déplacé hors de `Application.kt`) exposé sous `/api/v1`.
- Convention : **les endpoints métier vivent sous `/api/v1`** ; `/health/*` et `/` restent hors version (sondes d'infrastructure et smoke test).
- Toute future route métier s'enregistre dans `apiRoutes()`.

### `requestId` dans les logs

- `installRequestId()` extrait dans `common/requestid/RequestId.kt` — CallId n'est plus couplé au module d'erreurs.
- `CallLogging` configuré avec `mdc("requestId") { it.callId }`. ⚠️ Ktor 3.2.3 n'a **pas** `callIdMdc` (présent seulement dans la doc 3.6) : l'API réelle est `mdc(clé) { call -> valeur }`.
- `logback.xml` : pattern `… [%X{requestId}] …` → chaque ligne de log de requête porte l'identifiant.

Vérifié sur serveur réel (port 8082) :

```text
[e985c9e8-…] 200 OK: GET - /health/live in 94ms
[dbc01f6a-…] 404 Not Found: GET - /missing in 36ms
```

## Étape 3 — ce qui a été livré

### Validation des paramètres de requête

`common/validation/QueryParams.kt` — helpers qui valident et convertissent les query params, et lèvent `ValidationException` (400 + `details`) sinon :

| Helper | Rôle | Reasons possibles |
|---|---|---|
| `queryInt(name, default, min, max)` | entier borné | `invalid_format`, `out_of_range` |
| `queryOptionalInt(name, min, max)` | entier borné optionnel | idem |
| `queryBoolean(name, default)` | `true`/`false` uniquement | `invalid_format` |
| `queryText(name, maxLength, required)` | texte borné (trim) | `required`, `too_long` |
| `limitParam(default = 20, max = 50)` | limite de pagination standard | — |
| `cursorParam()` | curseur optionnel (≤ 256) | — |

Toute valeur répétée (`limit=1&limit=2`) est refusée : reason `duplicate_parameter`.
Seule la première erreur est signalée (pas encore d'agrégation multi-champs).

### Tests (8 ajoutés)

Défauts, valeurs valides, `limit=abc`, `limit=0`, `limit=1&limit=2`, `open=maybe`, `q` trop long, `minPrice=-1`.

## Étape 4 — ce qui a été livré

### Swagger UI + spécification versionnée

- `ktor-server-swagger-jvm` ajouté ; Swagger UI monté **sous le préfixe versionné** : `GET /api/v1/docs`.
- La spécification brute est servie juste à côté : `GET /api/v1/docs/openapi.yaml` (le plugin expose le fichier sur `<path>/<nom-du-fichier>`).
- Spec OpenAPI 3.0.3 écrite à la main : `backend/src/main/resources/openapi/openapi.yaml` — documente `/api/v1`, `/health/live`, `/health/ready` et les schémas partagés `ApiError` / `FieldError` / `ServiceInfo` / `HealthStatus`.

### ⚠️ Pourquoi la spec est statique, et non générée depuis les routes

La génération **code-first** (spéc déduite du code des routes) n'est pas possible dans notre configuration actuelle :

1. Elle passe par l'**extension compilateur OpenAPI**, livrée via le plugin Gradle `io.ktor.plugin` + bloc `ktor { openApi { } }`.
2. Cette extension **exige Kotlin 2.2.20** ; le projet est en **2.2.10** → risque d'erreur de compilation.
3. Elle n'apparaît qu'en **Ktor 3.6** ; nous sommes en **3.2.3**. Vérifié par inspection du jar 3.2.3 : `SwaggerConfig` ne contient que `version` / `packageLocation` / `customStyle` / `deepLinking` / `favicon` — **pas** de `OpenApiInfo` ni d'`OpenApiDocSource.Routing`.

⇒ Décision : spec **statique versionnée** en E0 (robuste, testable, zéro montée de version), et **réévaluation du code-first en E1** quand les routes métier existeront (elle implique d'accepter une mise à jour Kotlin ≥ 2.2.20 et/ou Ktor 3.6).

Détail à connaître : Swagger UI charge son CSS/JS depuis le CDN `unpkg.com` (swagger-ui-dist 5.17.12) — l'affichage dans le navigateur nécessite donc Internet, mais l'API et la spec fonctionnent hors ligne.

### Tests (2 ajoutés)

- `GET /api/v1/docs` → 200 + HTML Swagger.
- `GET /api/v1/docs/openapi.yaml` → 200 + YAML contenant `openapi: 3.0.3` et `/api/v1` (garde-fou contre une spec cassée/404).

## Étape 5 — ce qui a été livré

### PostgreSQL jetable dans les tests (Testcontainers)

- Dépendance ajoutée : **`org.testcontainers:testcontainers-postgresql:2.0.5`** (⚠️ en Testcontainers 2.0, les modules sont renommés : `org.testcontainers:postgresql` s'arrête à 1.21.4 ; le nouveau coordinat est `testcontainers-postgresql`), image `postgres:16-alpine`.
- `DatabaseFactory.initialize()` accepte désormais une **`DatabaseConfig` injectable** (défaut = `DatabaseConfig.fromEnvironment()`, donc aucun changement pour l'exécution normale). C'est ce qui permet aux tests de viser le conteneur au lieu de la base locale.
- Nouveau test `data/PostgresIntegrationTest.kt` — 3 cas :

  | Test | Ce qu'il garantit |
  |---|---|
  | migrations appliquées sur base vide + pool `isReady()` | Flyway joue bien `V1` sur une base vierge (`flyway_schema_history` non vide, table `zones` interrogeable) |
  | `zones` : UTF-8 + unicité | `city` prend le défaut `Lomé` (accents préservés) ; doublon `(name, city)` refusé avec SQLSTATE `23505` |
  | route `/health/ready` | Répond `200 {"status":"ok"}` quand la base répond (le test unitaire ne couvrait que le cas `503`) |

### Pourquoi Testcontainers 2.0.5 (et pas 1.21.x)

Docker Desktop ici expose un **Docker Engine 29.1.3** dont la `MinAPIVersion` est **1.44**. Testcontainers 1.21.3 (docker-java 3.4.2) négocie une API plus ancienne → l'engine répond `400 BadRequest` sur `/info`, et `DockerClientFactory.isDockerAvailable` reste `false` (tests systématiquement sautés, et la CI aurait échoué de la même façon).

Diagnostic mené :

- `docker --api-version` n'existe plus en CLI 29 ; `DOCKER_API_VERSION=1.32 docker info` reproduit exactement le `400` de Testcontainers, tandis que `1.44` et `1.52` répondent `29.1.3` → **cause = version d'API**.
- Testcontainers 2.0.5 embarque **docker-java 3.7.1**, qui gère Engine 29 (détection du pipe Desktop + API ≥ 1.44) **sans variable d'environnement ni fichier de config**.

⇒ Résultat : plus besoin de `DOCKER_HOST` ni de `~/.docker-java.properties` en local. Vérifié aussi avec `--no-daemon` (environnement vraiment propre) : **3 tests exécutés, 0 sautés, 0 échec**.

### Comportement sans Docker

Les 3 tests se **sautent automatiquement** si Docker est absent ou arrêté (`Assumptions.assumeTrue`, message explicite), pour ne pas casser le build d'un poste sans Docker. Avec Docker démarré : **23 tests exécutés** (20 unitaires + 3 d'intégration), 0 échec.

## Fichiers livrés (étapes 1 à 5)

| Fichier | Étape | Rôle |
|---|---|---|
| `backend/build.gradle.kts` | 1 | + `ktor-server-call-id-jvm`, `ktor-server-status-pages-jvm` |
| `backend/src/main/kotlin/…/common/error/ApiError.kt` | 1 | Payload d'erreur + catalogue `ErrorCodes` |
| `backend/src/main/kotlin/…/common/error/ApiException.kt` | 1 | Exceptions métier et leurs status HTTP |
| `backend/src/main/kotlin/…/common/error/ErrorHandling.kt` | 1 | `installErrorHandling()` (StatusPages) |
| `backend/src/main/kotlin/…/common/requestid/RequestId.kt` | 2 | `installRequestId()` (CallId) |
| `backend/src/main/kotlin/…/common/api/ApiRoutes.kt` | 2 | Prefix `/api/v1` + `ServiceInfo` |
| `backend/src/main/kotlin/…/common/validation/QueryParams.kt` | 3 | Validation des query params |
| `backend/src/main/resources/openapi/openapi.yaml` | 4 | Spec OpenAPI 3.0.3 (statique, versionnée) |
| `backend/src/main/kotlin/…/data/DatabaseFactory.kt` | 5 | `DatabaseConfig` injectable (tests Testcontainers) |
| `backend/src/main/kotlin/…/Application.kt` | 1–4 | Assemblage plugins + routes |
| `backend/src/main/resources/logback.xml` | 2 | Pattern avec `%X{requestId}` |
| `backend/src/test/kotlin/…/common/error/ErrorHandlingTest.kt` | 1 | 6 tests |
| `backend/src/test/kotlin/…/common/api/ApiRoutesTest.kt` | 2, 4 | 4 tests |
| `backend/src/test/kotlin/…/common/validation/QueryParamsTest.kt` | 3 | 8 tests |
| `backend/src/test/kotlin/…/data/PostgresIntegrationTest.kt` | 5 | 3 tests d'intégration (Docker) |
| `.github/workflows/backend-ci.yml` | 6 | CI backend (build + tests Testcontainers) |

Total : **23 tests** (20 unitaires + 3 d'intégration). Commande : `.\gradlew.bat -p backend test` (Docker démarré requis pour les 3 d'intégration, sinon sautés).

> Note : une erreur Gradle `BuildToolsApiClasspathEntrySnapshotTransform` est apparue après l'ajout des tests d'étape 3 ; `.\gradlew.bat -p backend clean test` l'a résolue (cache de snapshot corrompu). Faire un `clean` avant de conclure à une erreur de code.

## Étape 6 — ce qui a été livré

### Workflow GitHub Actions

`.github/workflows/backend-ci.yml` — déclenché sur `push` et `pull_request` quand `backend/**` ou le workflow lui-même change (sinon inutile), plus `workflow_dispatch` manuel.

| Élément | Choix | Pourquoi |
|---|---|---|
| Runner | `ubuntu-latest` | Docker présent → les tests Testcontainers tournent pour de vrai |
| JDK | Temurin **17** (`setup-java@v4`, `cache: gradle`) | aligné sur `jvmToolchain(17)` ; le cache évite de retélécharger les dépendances |
| Commande | `./gradlew -p backend build --no-daemon` | identique à celle validée en local ; `build` = compilation + tests + distribution |
| `chmod +x ./gradlew` | requis | ⚠️ le bit exécutable n'est **pas** dans git (fichier indexé en `100644`, créé sous Windows) |
| Rapport de tests | `upload-artifact@v4` (`if: always()`) | récupérer le HTML des tests après un échec en CI |
| `concurrency` + `timeout-minutes: 20` | annule les runs obsolètes, borne la durée | évite les runs qui s'empilent |

Vérifié en local : YAML valide (parse Python) et `.\gradlew.bat -p backend build --no-daemon` passe (BUILD SUCCESSFUL).

⚠️ **Non encore validé** : le workflow n'a jamais tourné sur GitHub (aucun push depuis son ajout). À confirmer au premier push ; E0 sera close quand le run sera vert.

## Prochaine étape — E1 (catalogue public)

1. Migrations des tables `categories`, `sellers`, `preparations` + seed (zones, catégories).
2. Endpoints `GET /api/v1/zones`, `/categories`, `/sellers` (`q`, filtres, tri curseur), `/sellers/{id}`, `/preparations`.
3. Réutiliser `QueryParams` (validation), `installErrorHandling()` (erreurs), `DatabaseConfig` (accès JDBC) et étendre la spec `openapi.yaml`.

