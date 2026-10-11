# TogoFood — Plan d’implémentation des endpoints

**Statut :** plan de livraison — décisions d'architecture tranchées (voir « Décisions tranchées »), contrats E0/E1 à valider avant développement  
**Socle choisi :** Kotlin + Ktor, PostgreSQL, API REST `/api/v1`  
**État au 10 octobre 2026 :** PostgreSQL, Ktor, Flyway, endpoints de santé et test de routes opérationnels ; aucun endpoint métier livré.

## Objectif

Livrer les endpoints dans un ordre qui permet de brancher l'app progressivement, garde la découverte utilisable sans compte et ne donne pas à un utilisateur la possibilité de modifier les données d'un autre vendeur. Chaque lot comprend son contrat, les migrations, le domaine, la persistance, les routes, les tests et la mise à jour OpenAPI.

## Décisions tranchées (10 octobre 2026)

| # | Sujet | Décision |
|---|---|---|
| D1 | Contrat zones | **Référentiel serveur avec IDs** : `GET /api/v1/zones` retourne `id`, `nom`, `ville`, `isActive` ; `sellers` et `preparations` filtrent par `zoneId`. L'app migre du nom dérivé vers l'id. |
| D2 | Note en E1 | **Colonnes dénormalisées** : `rating` et `review_count` sur `sellers` et `preparations`, seedés depuis les mocks. `minRating` disponible dès E1. Le recalcul depuis la table `reviews` est branché en E5. |
| D3 | Distance / nearby | **E1 n'expose pas `distanceMeters`** : le client calcule depuis le GPS de l'appareil si les coordonnées sont disponibles, sinon le filtre « À deux pas »/NEARBY est masqué. Coords vendeur et calcul serveur reportés (géolocalisation hors périmètre E1, cf. PLAN.md §8.8). |
| D4 | Socle technique E0 | **OpenAPI versionnée + Swagger UI** (spec **statique** en E0 — la génération code-first exige Kotlin ≥ 2.2.20 et Ktor 3.6, cf. E0-PROGRESS étape 4 ; réévaluée en E1), **SQL JDBC pur sans ORM** dans la couche data, **Testcontainers** pour les tests d'intégration PostgreSQL éphémères, **CI GitHub Actions** ajoutée à E0. |

## Ordre de livraison

| Lot | Endpoints/fonctionnalités | Dépend de | Critère de sortie |
|---|---|---|---|
| E0 — Contrat et infrastructure API | Version `/api/v1`, réponses et erreurs communes, validation, `requestId`, OpenAPI, harnoch PostgreSQL jetable, CI | Socle M0 | Contrat généré/validé, erreurs cohérentes testées, CI verte |
| E1 — Lecture publique | Zones, catégories, vendeurs, préparations, recherche et filtres | E0 ; schéma et seed catalogue | App peut remplacer les mocks de lecture sans compte |
| E2 — Identité | Demande/vérification OTP, session, renouvellement et déconnexion | E0 ; spécification M1 validée et décision fournisseur SMS | Sessions valides, OTP limité et aucun mode de test en production |
| E3 — Écriture vendeur | Onboarding/profil, préparations, disponibilité, horaires | E1, E2 ; règles de propriété validées | Un vendeur ne peut modifier que ses propres données |
| E4 — Favoris | Lecture, ajout et retrait de favoris synchronisés | E2 ; stratégie de migration des favoris invités | Opérations idempotentes et fusion locale testée |
| E5 — Avis/modération | Création/lecture d'avis, signalement, outils de modération, **recalcul des notes dénormalisées** | E1, E2 ; règles produit/modération validées | Publication, visibilité et retrait couverts par tests de permissions |
| E6 — Durcissement | Limites, métriques, charge, sauvegardes, TLS, déploiement | **E0–E5** ; décisions d'hébergement | Tests d'exploitation réussis et procédure de restauration documentée |

### Correspondance avec les jalons de PLAN.md

| Lot | Jalon PLAN.md | Remarque |
|---|---|---|
| E0 | M0 (restant) | Contrat, erreurs, requestId, OpenAPI, tests d'intégration, CI |
| E1 | M2 — Catalogue public | Livré avant E2 car la lecture publique ne dépend pas de l'identité |
| E2 | M1 — Identité | À valider : `docs/backend/M1-auth-spec.md` est encore un squelette |
| E3 | M3 — Espace vendeur | Bloqué par E2 |
| E4 + E5 | M4 — Favoris et avis | E5 conditionne le recalcul des notes (D2) |
| E6 | M5 — Durcissement | Couvre aussi les limites issues d'E5 |

## E0 — Contrat commun avant endpoints métier

### Choix techniques (D4)

- **OpenAPI** : spécification 3.0.3 maintenue dans `backend/src/main/resources/openapi/openapi.yaml`, servie versionnée (Swagger UI sur `/api/v1/docs`, YAML sur `/api/v1/docs/openapi.yaml`) et vérifiée par test en CI. Le **code-first** (génération depuis les routes via l'extension compilateur) est **différé à E1** : il impose Kotlin ≥ 2.2.20 (projet en 2.2.10) et Ktor 3.6 (projet en 3.2.3), et l'API `OpenApiDocSource`/`OpenApiInfo` est absente de `ktor-server-swagger` 3.2.3.
- **Validation** : plugin de validation Ktor pour les entrées (query params, corps JSON) ; les règles métier restent dans les cas d'utilisation.
- **Erreurs** : plugin `StatusPages` unique, transformant les erreurs applicatives au bord HTTP (aucun `respond` d'erreur en clair dans les routes).
- **Accès SQL** : SQL JDBC pur dans des DAO de la couche `data` (pas d'ORM), dans la continuité de `DatabaseFactory`.
- **Tests d'intégration** : base PostgreSQL éphémère via **Testcontainers**, utilisée par les tests de routes qui touchent la base.
- **CI** : workflow GitHub Actions — build, tests unitaires et d'intégration (Testcontainers), vérification que les migrations s'appliquent sur une base vide.

### Travail

1. Ajouter `/api/v1` et exposer la spécification OpenAPI versionnée (générée, vérifiée dans la CI).
2. Fixer la forme JSON des ressources, les noms des champs, les conventions UUID, les dates ISO-8601 et les prix entiers en FCFA.
3. Uniformiser les erreurs, par exemple :

   ```json
   {
     "code": "VALIDATION_ERROR",
     "message": "La requête contient des champs invalides.",
     "requestId": "…",
     "details": [{ "field": "zoneId", "reason": "invalid_format" }]
   }
   ```

4. Valider et limiter les paramètres de requête, définir le tri et une pagination stable par curseur (voir clés de tri en E1).
5. Associer chaque requête à un `requestId` (header de réponse + logs), sans journaliser les OTP, jetons, numéros complets ou secrets.
6. Définir dès E0 le code `RATE_LIMITED` (429) dans le catalogue des erreurs ; **l'activation des limites et leurs tests arrivent en E2**.
7. Tester erreurs 400/404/409/500, sérialisation JSON, contrat OpenAPI et migrations sur base vide.

**Terminé quand :** les endpoints utilisent tous les mêmes formats, un client peut distinguer erreur de validation, absence de ressource et panne serveur, et la CI exécute build + tests + migrations.

## E1 — Lecture publique du catalogue (premier lot métier)

### Routes

```text
GET /api/v1/zones
GET /api/v1/categories
GET /api/v1/sellers?zoneId=&q=&cursor=&limit=
GET /api/v1/sellers/{sellerId}
GET /api/v1/preparations?zoneId=&categoryId=&available=&open=&minRating=&minPrice=&maxPrice=&q=&cursor=&limit=
GET /api/v1/preparations/{preparationId}
```

### Travail

1. Compléter les migrations des catégories, vendeurs, préparations, variantes et horaires ; **zones : le référentiel est en place (V1), l'exposer via l'API (D1)**. Pas d'interface d'administration en E1 : zones et catégories sont alimentées par le seed (arbitrage d'administration reporté).
2. Créer un seed local déterministe à partir des données mock actuelles, avec identifiants stables et relations valides, incluant `rating`/`review_count` seedés (D2) et le drapeau `isPopular` (seedé depuis les mocks ; toute règle de dérivation server-side reste reportée).
3. Implémenter les interfaces domaine et dépôts PostgreSQL ; isoler les requêtes SQL du routage.
4. Mapper les modèles PostgreSQL vers des DTO publics ; ne pas exposer de champs privés (téléphone propriétaire, données de compte, traces internes) **ni `distanceMeters` (D3)**.
5. Fixer les sémantiques de filtre :
   - `available` : état de la préparation + fraîcheur (`availability_checked_at`) ; signaler l'inconnu/vieille donnée.
   - `open` : horaires du vendeur en fuseau `Africa/Lome`, **en explicitant la précédence entre le toggle hub (Ouvert/Fermé) et le planning** — règle à figer dans le contrat, elle existe déjà côté app (`Seller.useSchedule`).
   - `q` : recherche insensible aux cas sur nom de préparation **et nom de vendeur** (les deux listes sont paginées côté serveur, le filtrage client n'est pas possible avec un curseur).
   - `minRating` : sur les colonnes dénormalisées seedées (D2).
6. Définir les clés de tri stables de la pagination curseur : `sellers` et `preparations` → `(name ASC, id ASC)` ; `zones` et `categories` → `(name ASC, id ASC)`.
7. Gérer les côtés Android de D1/D3 : migration du sélecteur de zone vers `zoneId`, masquage du filtre nearby quand les coords sont indisponibles.
8. Ajouter les tests des filtres combinés, des variantes multiples, des zones inexistantes/inactives, des listes vides, des limites de page et du seed.

**Sortie Android :** remplacer en premier les dépôts mock de Home, Search, Map, SellerDetail et PreparationDetail pour leurs lectures. Le catalogue reste accessible sans session. Prérequis Android : arbitrage de la stack réseau (client HTTP, sérialisation, DI — absents de `app/build.gradle.kts` aujourd'hui) avant branchement.

**Points à ne pas supprimer du contrat :**

- les mocks n'ont pas de coordonnées GPS réelles ; `distanceMeters` n'est pas exposé tant qu'une règle de localisation n'est pas choisie (D3) ;
- `rating`/`review_count` sont des valeurs seedées jusqu'à E5, pas des agrégats vivants (D2).

## E2 — Authentification téléphone

### Routes candidates

```text
POST /api/v1/auth/otp
POST /api/v1/auth/verify
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET  /api/v1/me
```

### Travail

1. Compléter et faire approuver `docs/backend/M1-auth-spec.md` (actuellement squelette) : corps des requêtes/réponses, rôles, erreurs, durée des OTP/tokens, limites et parcours client/vendeur.
2. Normaliser les numéros en **E.164 avec indicatif `+228`** (règle fixée par PLAN.md §3), avant toute écriture ou comparaison.
3. Décider le fournisseur SMS et son intégration ; définir une interface pour pouvoir changer de fournisseur.
4. Persister uniquement l'empreinte de l'OTP, l'expiration, les essais et l'état ; effacer/consommer le challenge après validation.
5. Ajouter limites par numéro et adresse réseau (activation du code `RATE_LIMITED` défini en E0), réponses anti-énumération et délais d'expiration.
6. Définir les access/refresh tokens, leur signature/stockage, rotation, révocation et stratégie de déconnexion.
7. Interdire les OTP fixes, secrets intégrés ou bypass en production ; fournir un adaptateur simulé uniquement en environnement local/test.
8. Tester succès, code incorrect/expiré, répétition, dépassement de limite (429), rotation/révocation, numéro inconnu et statut/rôle.

**Garde de sortie :** aucun endpoint d'écriture vendeur ou favori n'est rendu accessible avant validation de l'identité et des rôles.

## E3 — Écriture et espace vendeur

### Routes candidates

```text
POST   /api/v1/vendor/onboarding
GET    /api/v1/vendor/me
PATCH  /api/v1/vendor/me
POST   /api/v1/vendor/preparations
PATCH  /api/v1/vendor/preparations/{preparationId}
DELETE /api/v1/vendor/preparations/{preparationId}
PUT    /api/v1/vendor/preparations/{preparationId}/availability
PUT    /api/v1/vendor/schedule
```

La création de vendeur devra être arbitrée : route onboarding dédiée ou création après OTP avec étape de profil. Ne pas laisser un client attribuer lui-même le rôle vendeur.

### Travail

1. Fixer la cardinalité compte↔vendeur et le processus d'approbation/vérification.
2. Authentifier chaque mutation ; contrôler rôle, propriétaire, statut du compte et existence de la ressource côté serveur.
3. Valider champs profil, **zone active par `zoneId` (D1)**, catégorie, prix > 0, variantes, horaires et état de disponibilité.
4. Faire les écritures multi-tables (préparation + variantes, planning + créneaux) dans une transaction.
5. Ajouter mises à jour conditionnelles/versionnage si nécessaire pour éviter d'écraser une modification concurrente.
6. Tester les rôles invité/client/propriétaire/autre vendeur/admin ainsi que l'intégrité après échec au milieu d'une transaction.

**Sortie Android :** remplacer progressivement les opérations `CatalogRepository` en mémoire du SellerHub et des écrans d'édition.

## E4 — Favoris synchronisés

### Routes candidates

```text
GET    /api/v1/me/favorites?cursor=&limit=
PUT    /api/v1/me/favorites/{targetType}/{targetId}
DELETE /api/v1/me/favorites/{targetType}/{targetId}
```

1. Décider les types de cible autorisés (préparation et vendeur).
2. Ajouter les clés étrangères et l'unicité utilisateur/type/cible.
3. Rendre PUT/DELETE idempotents ; pagination curseur sur `(created_at DESC, id DESC)`.
4. Définir puis implémenter la fusion du stockage local invité après connexion, en cas de conflit sans doublons.
5. Tester la pagination, la répétition, les ressources supprimées et l'accès aux favoris d'un autre compte.

## E5 — Avis et modération (après arbitrage produit)

Routes candidates à spécifier, sans engagement avant décision :

```text
GET  /api/v1/sellers/{sellerId}/reviews
POST /api/v1/sellers/{sellerId}/reviews
POST /api/v1/reviews/{reviewId}/reports
```

Avant de coder : définir qui peut publier, si une expérience vérifiée est exigée, limites de fréquence, modifications/suppressions, modération préalable ou après publication, règles de signalement et recours. Ajouter ensuite les rôles/modérations internes via des endpoints séparés et non publics.

**Livrable supplémentaire (D2) :** brancher le recalcul de `rating`/`review_count` (dénormalisés seedés en E1) sur la table `reviews`, avec une procédure de rattrapage vérifiable.

## E6 — Durcissement et exploitation

Dépend de **E0 à E5** : limites de requêtes et métriques doivent couvrir l'ensemble des routes publiques et authentifiées, avis compris.

1. Choisir hébergement API/PostgreSQL, domaine, TLS, sauvegardes et procédure de restauration.
2. Ajouter métriques, alertes, politique de rétention et journalisation sans données personnelles sensibles.
3. Tester charge, restauration, réseau faible et compatibilité avec la version d'API Android.

## Règles d'implémentation par endpoint

- Route Ktor : HTTP, extraction/validation d'entrée et conversion en réponse seulement.
- Application : cas d'utilisation et transactions, sans dépendance HTTP/SQL.
- Domaine : modèles métier, règles et interfaces de dépôt.
- Data : DAO/JDBC, DTO d'infrastructure et mappers PostgreSQL.
- Une erreur métier attendue est représentée par le résultat du cas d'utilisation et transformée au bord HTTP en code d'erreur commun.
- Pour chaque route : test du cas heureux, validation invalide, absence de ressource, base indisponible pertinente, autorisation et régression de contrat.
- Les listes paginées exposent une clé de tri stable ; tout filtre combiné à un curseur est testé pour l'absence de doublons/d'oublis entre pages.
- Pas de migration destructive sans migration de transition, sauvegarde, vérification des données et plan de retour arrière.

## Intégration Android à chaque lot

1. Publier et figer le contrat OpenAPI du lot.
2. **Arbitrer puis installer la stack réseau Android** (client HTTP, sérialisation JSON, DI) — aucune de ces dépendances n'existe dans `app/build.gradle.kts` ; décision à prendre avant le branchement E1.
3. Ajouter les DTO réseau et mappers dans la couche data Android ; les modèles réseau ne traversent pas vers Compose.
4. Implémenter un dépôt distant derrière l'interface du domaine (à créer : `domain/` ne contient aujourd'hui que des modèles).
5. Garder les ViewModels indépendants du client réseau et des détails JSON.
6. Couvrir loading/error/empty/success, réseau faible et lecture cache ; n'enlever les mocks qu'après parité vérifiée.
7. Livrer le changement par fonctionnalité, avec configuration d'URL par build type et TLS hors développement local.

## Definition of Done pour un lot

- Contrat documenté dans OpenAPI et testé contre les exemples.
- Migration Flyway testée depuis une base vide et depuis le dernier schéma.
- Tests unitaires domaine et tests d'intégration routes + vraie instance PostgreSQL jetable (Testcontainers depuis E0).
- CI verte : build, tests, migrations — workflow `.github/workflows/backend-ci.yml` (écrit en E0, à valider au premier push).
- Vérification des permissions négatives, du format d'erreur et des données non divulguées (ni téléphone propriétaire, ni notes presenting comme vivantes avant E5, ni distance calculée serveur avant décision geo).
- Logs/mesures utiles sans secrets ni données personnelles excessives.
- Android connecté au lot avec gestion des erreurs et états hors ligne, ou lot explicitement backend-only.
- Documentation de démarrage et d'exploitation mise à jour.

## Prochaine étape recommandée

**E0 est livré** (voir `docs/backend/E0-PROGRESS.md` : erreurs communes, `/api/v1`, validation, `requestId`, OpenAPI + Swagger UI, harnais Testcontainers, CI). Prochaine étape : **E1**, à faire valider — il intègre D1 (zones par id), D2 (notes dénormalisées) et D3 (pas de `distanceMeters` serveur). L'authentification SMS n'est pas un prérequis à la lecture publique du catalogue ; elle devient impérative avant toute mutation personnelle ou vendeur.
