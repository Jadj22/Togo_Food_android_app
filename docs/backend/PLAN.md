# TogoFood — Plan du backend

**Statut :** plan de cadrage — aucune implémentation backend commencée  
**Décisions prises :** API REST personnalisée, Kotlin + Ktor, PostgreSQL  
**Références :** `.agents/PRD.md`, `.agents/skills/android-clean-mvvm-compose/SKILL.md`, `project_evolution.md`

## 1. Objectif et principes

Remplacer progressivement les dépôts et données mockés de l'application Android par un service backend fiable, sans imposer de compte pour découvrir les plats et les vendeurs.

- Discovery locale d'abord : plats, vendeurs, zones, disponibilité, prix et horaires.
- Ne pas construire panier, commande, paiement ou livraison dans ce périmètre.
- Le serveur est la source de vérité des données partagées et des règles d'accès.
- Le client Android garde une architecture `presentation → domain ← data` : les ViewModels dépendent des interfaces de dépôt du domaine, et le réseau ainsi que ses DTO restent dans la couche data.
- Les contrats REST sont versionnés sous `/api/v1` et documentés avant leur implémentation.
- Les données publiques doivent rester consultables sans authentification ; seuls les parcours qui modifient des données exigent une identité.

## 2. Architecture proposée

Backend autonome dans `backend/`, déployable indépendamment de l'application Android :

```text
backend/
  src/main/kotlin/com/example/togofood/
    app/              # démarrage Ktor et assemblage
    common/           # erreurs, réponses, configuration, logs
    identity/
      api/            # routes et contrats HTTP
      application/    # cas d'utilisation
      domain/          # modèles, règles et interfaces de dépôt
      data/            # PostgreSQL, DTO et implémentations
    catalog/           # vendeurs, préparations, variantes, catégories et zones
    favorites/         # favoris du compte
    reviews/           # avis et modération
    vendor/            # opérations authentifiées du vendeur
  src/main/resources/db/migration/  # migrations Flyway
  src/test/                         # tests unitaires et d'intégration
```

Flux d'une requête : route Ktor → validation/authentification → cas d'utilisation → interface de dépôt du domaine → implémentation PostgreSQL. Les routes ne contiennent ni requêtes SQL ni règles métier.

Socle technique recommandé :

- Ktor (routing, sérialisation JSON, authentification, validation et tests).
- PostgreSQL avec migrations Flyway ; accès SQL via Exposed ou équivalent après un petit prototype de validation.
- Docker Compose pour le développement local et les tests d'intégration.
- Configuration par variables d'environnement ; secrets absents du dépôt.
- OpenAPI comme contrat vérifiable et documentation des routes.

## 3. Modèle de données initial

Les noms et relations exacts seront figés dans les spécifications de chaque module avant migration.

| Entité | Données et règles principales |
|---|---|
| `users` | Identifiant, téléphone normalisé E.164 (+228), état du compte, dates de création et mise à jour |
| `user_roles` | Rôles séparés du profil ; accès public invité distinct des rôles client/vendeur/admin |
| `otp_challenges` | Empreinte du code, expiration, tentatives, état et données de limitation ; jamais stocker le code en clair |
| `refresh_tokens` | Empreinte révocable, expiration et rotation |
| `zones` | Identifiant stable, nom affiché, ville et indicateur actif ; alimentera le sélecteur de zone de l'accueil |
| `sellers` | Propriétaire, identité, téléphone, zone, repère, statut, vérification, fraîcheur et horaires |
| `categories` | Identifiant, libellés et métadonnées d'affichage |
| `preparations` | Vendeur, catégorie, nom, description, URL média, disponibilité et dates de vérification |
| `preparation_variants` | Préparation, libellé et prix entier en FCFA ; plusieurs formats possibles |
| `seller_schedule_slots` | Jour de semaine, ouvert/fermé et créneaux ; interprétés dans le fuseau `Africa/Lome` |
| `favorites` | Relation utilisateur-cible ; contrainte unique pour éviter les doublons |
| `reviews` | Auteur, vendeur/préparation ciblé, note, texte, statut de modération et dates |
| `moderation_reports` | Signalements, motif, état de traitement et trace d'audit |

Les statuts vendeur, horaires et disponibilité d'une préparation restent trois notions distinctes. Une donnée de disponibilité inclut son horodatage afin que l'app puisse signaler une information ancienne, conformément au principe « Reality First » du PRD.

## 4. Contrat HTTP de base

- Préfixe `/api/v1`; JSON UTF-8; dates ISO-8601 en UTC; monnaie FCFA en entier.
- Succès : ressources explicites et pagination stable (`items`, `nextCursor` ou pagination documentée).
- Erreur uniforme : `code`, `message` localisable côté app, `requestId` et détails de validation non sensibles.
- Codes HTTP cohérents : `400` validation, `401` non authentifié, `403` interdit, `404` absent, `409` conflit, `429` limite dépassée, `5xx` erreur serveur.
- Les routes de lecture du catalogue et des zones sont publiques. Les routes de modification vérifient côté serveur l'identité, le rôle et la propriété de la ressource.

### Routes candidates pour le premier contrat

```text
GET    /api/v1/zones
GET    /api/v1/categories
GET    /api/v1/preparations?zoneId=&categoryId=&available=&open=&minRating=&minPrice=&maxPrice=&cursor=
GET    /api/v1/preparations/{preparationId}
GET    /api/v1/sellers?zoneId=&cursor=
GET    /api/v1/sellers/{sellerId}

POST   /api/v1/auth/otp
POST   /api/v1/auth/verify
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout

GET    /api/v1/me
GET    /api/v1/me/favorites
PUT    /api/v1/me/favorites/{targetType}/{targetId}
DELETE /api/v1/me/favorites/{targetType}/{targetId}

GET/PATCH /api/v1/vendor/me
POST      /api/v1/vendor/preparations
PATCH     /api/v1/vendor/preparations/{preparationId}
DELETE    /api/v1/vendor/preparations/{preparationId}
PUT       /api/v1/vendor/preparations/{preparationId}/availability
PUT       /api/v1/vendor/schedule
```

Les routes d'avis, de signalement et d'administration seront ajoutées après validation de leurs règles de modération. Le contrat précis (filtres, tri, pagination, champs DTO) doit être décrit en OpenAPI et dans les spécifications de module avant développement.

## 5. Phases et dépendances

### M0 — Socle backend

1. Créer le service Ktor indépendant et les environnements local/test/production.
2. Ajouter PostgreSQL local, Flyway, contrôles de santé (`/health/live`, `/health/ready`) et gestion de configuration.
3. Définir le format d'erreur, les logs structurés avec `requestId`, les règles de validation et le contrat OpenAPI.
4. Ajouter tests unitaires, tests de routes et test d'intégration avec une base PostgreSQL jetable.
5. Ajouter CI (build, tests, migrations) et procédure de démarrage local.

**Sortie attendue :** service démarrable sans secrets, base migrée automatiquement en local et endpoints de santé testés.

### M1 — Identité et authentification téléphone

1. Compléter et faire valider `docs/backend/M1-auth-spec.md` (document présent mais encore vide hors sommaire).
2. Spécifier création/demande OTP, vérification, émission/renouvellement/révocation des jetons et rattachement du rôle vendeur.
3. Isoler le fournisseur SMS derrière une interface ; mode de développement non activable en production.
4. Implémenter limites de fréquence, expiration, nombre d'essais, anti-énumération et rotation des refresh tokens.
5. Tester les parcours client/vendeur, les erreurs et les accès non autorisés.

**Sortie attendue :** actions personnelles sécurisées ; aucune connexion requise pour la découverte publique.

### M2 — Catalogue public et découverte locale

1. Définir les contrats zones, catégories, vendeurs, préparations, variantes, horaires et disponibilité.
2. Importer les mocks existants dans un seed déterministe pour faciliter comparaison et tests.
3. Implémenter listes/détails, filtres de zone/catégorie/prix/disponibilité/ouverture/note et pagination.
4. Ajouter la recherche et les index nécessaires seulement après mesure sur les requêtes attendues.
5. Garantir les tests de fraîcheur de disponibilité, zone inexistante et catalogue vide.

**Sortie attendue :** remplacement progressif de `Mock*Repository` et `CatalogRepository` côté lecture.

### M3 — Espace vendeur

1. Définir le parcours de création et de validation d'un profil vendeur.
2. Restreindre toutes les mutations au vendeur propriétaire ou à un administrateur autorisé.
3. Implémenter édition profil/zone/repère, CRUD des préparations et variantes, disponibilité et horaires.
4. Ajouter validation métier et traces d'audit pour les changements de statut.

**Sortie attendue :** le hub vendeur persiste sur le serveur et les changements sont visibles dans le catalogue public.

### M4 — Favoris et avis

1. Synchroniser les favoris serveur pour les comptes, avec opérations idempotentes.
2. Prévoir la migration des favoris invités locaux vers le compte après connexion.
3. Définir puis implémenter création/édition éventuelle d'avis, limites de fréquence, signalements et modération.

**Sortie attendue :** état favoris cohérent entre appareils ; avis uniquement lorsque le processus de modération est défini.

### M5 — Durcissement et mise en production

1. Choisir hébergement API/PostgreSQL, domaine, TLS, sauvegardes et procédure de restauration.
2. Choisir le fournisseur SMS adapté au Togo et tester délivrabilité/coût.
3. Ajouter métriques, alertes, limites de requêtes, politique de rétention et journalisation sans données personnelles sensibles.
4. Tester charge, restauration, réseau faible et compatibilité avec la version d'API Android.

## 6. Intégration Android selon le skill MVVM/Clean Architecture

La migration se fait par fonctionnalité, sans basculer l'app entière en une fois :

1. Définir les interfaces de repository dans `domain`; conserver les modèles métier distincts des DTO HTTP.
2. Créer clients Retrofit (ou client retenu) et DTO/mappers dans `data/remote` et `data/mapper`.
3. Implémenter les dépôts distants derrière ces interfaces ; les ViewModels n'importent jamais Ktor/Retrofit ni les classes `data`.
4. Garder les écrans `Content` testables par état et callbacks ; utiliser des erreurs typées traduites en messages UI.
5. Migrer par ordre : lecture zones/catalogue → détails/search → auth → seller hub → favoris/avis.
6. Ajouter Room comme cache de lecture pour offline basique et cache-first lorsque les contrats catalogue sont stables ; l'app reste utile sans réseau.
7. Retirer les mocks d'une fonctionnalité uniquement après tests de non-régression et vérification des états réseau/erreur/vide.

Le skill décrit Hilt, Retrofit et Arrow comme conventions possibles, mais l'application actuelle n'a pas ces dépendances dans `app/build.gradle.kts`. La décision d'ajouter un conteneur DI et la bibliothèque de résultat côté Android sera prise pendant le cadrage de l'intégration, pas ajoutée implicitement au backend.

## 7. Qualité et critères de validation

- Chaque migration est versionnée, relisible et testée sur base vide ainsi que depuis la migration précédente.
- Tests unitaires des règles métier, tests de routes/authentification et tests d'intégration PostgreSQL.
- Tests de permissions pour chaque route de mutation (invité, client, autre vendeur, propriétaire, admin).
- Tests Android des mappers, dépôts fake/remote et ViewModels ; tests UI sur les états chargement, erreur, vide et succès.
- Mesurer les objectifs du PRD (API simple < 300 ms, recherche < 500 ms) sur un environnement et jeu de données documentés ; ne pas les déclarer atteints sans mesure.
- Vérifier qu'aucune réponse/log ne divulgue OTP, access token, refresh token ou données personnelles inutiles.

## 8. Décisions encore ouvertes avant implémentation

1. **Hébergement** API et PostgreSQL : région, coûts, sauvegarde, restauration et disponibilité.
2. **SMS OTP** : fournisseur réellement disponible et tarifairement viable au Togo ; politique de secours.
3. **Accès données** : choisir Exposed ou une approche SQL légère après prototype M0.
4. **Images** : stockage objet et limites de fichiers ; ne pas stocker les binaires dans PostgreSQL.
5. **Compte vendeur** : un compte peut-il posséder plusieurs boutiques, ou une seule ?
6. **Avis** : qui peut publier, preuve de visite éventuelle, règles de retrait/modération.
7. **Favoris invités** : format et moment exact de fusion locale lors de la connexion.
8. **Recherche géographique** : le premier filtre utilise les zones déclarées ; GPS/coordonnées et calcul de distance restent hors M2 sauf décision produit explicite.

Ne pas commencer M1/M2 en production tant que les choix bloquants du module concerné et le contrat correspondant ne sont pas validés.
