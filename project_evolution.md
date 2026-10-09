# TogoFood - Évolution du Projet

Ce document retrace l'historique des modifications architecturales et fonctionnelles du projet TogoFood. Il sert de point de référence pour comprendre l'état actuel de l'application et les standards mis en place.

## Vue d'Ensemble
* **Application** : TogoFood (Livraison de street-food à Lomé, Togo)
* **Stack Technique** : Kotlin, Jetpack Compose, Navigation Compose
* **Architecture** : MVVM (Model-View-ViewModel), Single Responsibility Principle, SOLID
* **Langue & Localisation** : Français, numéros Togolais (+228, 8 chiffres)

---

## 📅 Historique des développements

### Étape 1 : Refonte Architecturale & Navigation
* Remplacement d'une UI monolithique de 550 lignes (`MainActivity`) par une architecture propre.
* **Navigation Compose** (`NavHost`, `NavGraph`) ajoutée pour remplacer la navigation par état manuel.
* **Structure des dossiers** mise en place :
  * `ui/TogoFoodApp.kt` : Racine de l'UI.
  * `ui/navigation/NavGraph.kt` : Gestion des routes (Login, Register).
  * `ui/screens/auth/` : Écrans d'authentification.
* Résolution d'un crash immédiat au lancement lié au `SplashScreen` (méthode fantôme supprimée).

### Étape 2 : UI/UX & Composants d'Authentification
* Ajout d'icônes vectorielles SVG converties en XML (Google avec `Color.Unspecified` pour garder les couleurs, Facebook, mot de passe).
* **Inscription Multi-Étapes avec animations** :
  * **Client** : Étape 1/1 unique.
  * **Vendeur** : Étape 1/2 (identifiants) avec message contextuel, puis Étape 2/2 (infos boutique).
* Création de `OtpVerificationBottomSheet.kt` : Modale BottomSheet Material3 pour le code OTP, incluant 4 cases personnalisées et un compte à rebours de 42s.

### Étape 3 : Standards de Développement (MVVM & Clean Code)
* **ViewModels (MVVM)** : L'état de l'interface (variables temporaires, saisies) a été extrait des Composables vers `LoginViewModel.kt` et `RegisterViewModel.kt` utilisant des `StateFlow`.
* **Design System** : Création du package `ui/components/` contenant des atomes d'UI réutilisables :
  * `Buttons.kt` : `PrimaryButton`, `SocialLoginButton`.
  * `TextFields.kt` : `LabeledTextField`, `PhoneTextField`, `PasswordField` (tous gèrent un état `isError` et `errorMessage`).
* **Validation des formulaires** : Ajout de la logique de vérification dans les ViewModels (numéro à 8 chiffres obligatoire, etc.).
* **Internationalisation (`strings.xml`)** : Éradication des textes "en dur" dans le code. Tout est centralisé dans `res/values/strings.xml`.

### Étape 4 : Optimisation des Animations & UI
* **Animations d'États Ciblées** : Remplacement de l'animation globale de la page (`AnimatedContent` autour du composable entier) par une approche déclarative ciblée (`AnimatedVisibility` et `animateContentSize`). Seuls les champs qui diffèrent entre Client et Vendeur s'affichent ou se masquent, empêchant le rechargement/glissement complet de la page.
* **Bouton Retour** : Remplacement du bouton texte en dur par un vrai composable circulaire contenant une icône vectorielle standard (`ic_arrow_back.xml`).

### Étape 5 : Micro-interactions & UX (Le "Juice")
* **États de Chargement** : Le `PrimaryButton` intègre un état `isLoading` qui affiche un `CircularProgressIndicator` natif et bloque les clics multiples.
* **Retours Haptiques (Vibrations)** : Utilisation de `LocalHapticFeedback` pour donner une confirmation physique lors de la saisie complète du code OTP et lors de l'appui sur Confirmer.
* **Animation d'Échec (Shake Effect)** : En cas de mauvais code OTP, une animation `Animatable` fait trembler le composant de gauche à droite, avec une vibration lourde (`LongPress`) et le nettoyage automatique des cases pour forcer la ré-entrée (le code "1234" simule un succès, les autres une erreur).

### Étape 6 : Navigation Principale & Écran d'Accueil (Phase 2)
* **Couche Domaine** : Création des modèles métier purs (`Preparation`, `Seller`, `Category`, `Variant`, `AvailabilityStatus` avec 5 états distincts).
* **Couche Data (Mock)** : `MockData.kt` avec 5 vendeurs et 10 plats de Lomé (Tokoin, Bè, Agoè, Adidogomé) + prix en FCFA réalistes. Repositories interfacés (`PreparationRepository`, `SellerRepository`, `CategoryRepository`) — prêts pour être remplacés par Retrofit.
* **BottomNavigationBar** : `BottomNavItem.kt` (sealed class) + `MainScreen.kt` avec 5 tabs Material3 colorés en orange.
* **Navigation Auth → Main** : Back stack d'authentification entièrement effacée à la connexion. L'utilisateur ne peut plus revenir à Login via le bouton retour.
* **Écrans Placeholder** : Search, Favorites, Alerts, Profile.
* **Composants Design System** : `AvailabilityBadge.kt` (accessible : couleur + texte), `SectionHeader.kt`, `PreparationCard.kt`.
* **HomeScreen** : Accueil complet scrollable avec Greeting, SearchBar CTA, filtre par catégorie (chips), et 3 sections horizontales (Près de toi, Disponible maintenant, Populaire). Géré par `HomeViewModel`.

### Étape 7 : Refonte UI & Architecture (Inspiration Bolt Food)
* **Audit UI complet** : Alignement de l'esthétique sur les standards modernes (design sans bordures, composants aérés, typographie hiérarchisée).
* **Nettoyage des Strings** : Extraction de tous les textes en dur de `HomeScreen` et des composants associés vers `res/values/strings.xml`.
* **Remplacement des icônes Emojis** : Utilisation de vraies icônes vectorielles `Material Icons` (Recherche, Localisation, Favoris, etc.).
* **Respect du PRD** : Suppression de l'icône de livraison ("vélo") remplacée par un marqueur de distance (le PRD excluant la livraison).
* **Composants mis à jour** : 
    - `LocationHeader` : Top bar épurée.
    - `SearchBar` : Forme "Pill", comportement de "Fake Input" pour fluidité.
    - `PreparationCard` : Layout compact, image dominante, badges superposés, ajout du callback `onFavoriteClick`.
    - `WidePromoCard` : Fonds pastels, aucune surcouche sombre pour maximiser la clarté.

### Étape 8 : Écran Détail Vendeur (Style Bolt Food)
* **Architecture** : Création de `SellerDetailViewModel` pour charger les infos du vendeur et la liste de ses plats via son ID.
* **Navigation** : Ajout de la route `seller_detail/{sellerId}` dans `NavGraph.kt` et connexion des clics depuis `HomeScreen`.
* **Design UI** :
    - `MenuItemRow` : Layout en liste (texte à gauche, image carrée à droite) pour le menu.
    - `SellerDetailScreen` : En-tête avec image `Cover` prenant 30% de l'écran, boutons flottants (Retour, Favori, Partage), et une "Sheet" blanche aux bords arrondis qui remonte par-dessus l'image pour afficher les informations et le menu.

### Étape 9 : Écran de Recherche & Filtres (Style Bolt Food)
* **Architecture** : Création du `SearchViewModel` gérant l'état de la recherche (requête, résultats, filtres, chargement).
* **Design UI** :
    - `SearchTextField` : Barre de saisie en haut avec auto-focus du clavier.
    - **État vide** : Affichage d'une grille de catégories attrayantes (Riz, Fufu, etc.).
    - **Filtres rapides** : "Chips" horizontaux sous la barre de recherche (ex: Ouvert, Populaire).
    - **Résultats** : Liste verticale des plats trouvés, mettant en évidence le nom du vendeur et la distance selon le PRD.
* **Navigation** : Câblage de la `SearchBar` de l'accueil vers la route de recherche.

### Étape 10 : Écran Carte Immersif & Refonte Navigation
* **Refonte Navigation (Apple Design Guidelines)** : Factorisation de la BottomNavigation de 5 à 4 onglets (suppression de l'onglet Alertes/Notifications pour réduire la complexité cognitive).
* **Création MapScreen** :
    - Vue immersive plein écran (sans bottom nav).
    - Canvas interactif simulant une carte (Pan gesture).
    - Affichage des `Seller` via des `MapMarker` cliquables.
    - **Progressive Disclosure** : Modale (`AnimatedVisibility`) affichant un résumé du restaurant au clic sur un marqueur, qui permet de naviguer vers le détail vendeur.

### Étape 11 : Page Préparation — Refonte selon PRD §19
* **Refonte UI** : remplacement de l'écran détail (hero vide + "Ajouter au panier" non conforme P10) par une page répondant aux 6 questions fondamentales (QUOI / COMBIEN / DISPONIBLE / CHEZ QUI / OÙ / CONFIANCE).
* **Actions réelles (PRD §48)** : `Appeler` (ACTION_DIAL), `Itinéraire` (geo:), `Partager` (ACTION_SEND), `Commander sur WhatsApp` (wa.me pré-rempli avec portion choisie, fallback appel).
* **Contenu** : ligne de disponibilité unique avec fraîcheur (P3 Reality First), liste de portions (`PriceRow`, sans sélection fausse puisque aucun panier — P10), carte vendeur enrichie (✔ vérifié, note, cliquable en entier).
* **Accessibilité** : `WindowInsets.statusBars` / `navigationBars` (fin des barres système), contrastes revus, locales figées (`Locale.US`) pour prix et distances.

### Étape 12 : Favoris (PRD §25) + Refactoring DRY
* **DRY** : création de `FoodIcons` (emoji + fonds pastels) et `FormatUtils` (`formatFcfa`, `formatDistance`) — doublons supprimés de `PreparationCard`, `MenuItemRow`, `PreparationDetailScreen`, `SellerDetailScreen`.
* **FavoritesRepository** : singleton en mémoire (`StateFlow<Set<String>>` + `toggle`), surface publique stable pour basculer vers DataStore/Firebase plus tard.
* **FavoritesViewModel + FavoritesScreen** : états vide (CTA "Découvrir") / rempli (grille 2 colonnes, retrait immédiat depuis la carte).
* **Câblage** : cœurs actifs sur l'accueil (Populaire / À proximité) et sur le hero de la page préparation ; navigation Favoris → détail.

### Étape 13 : Profil (PRD §33/§34)
* **Mode invité par défaut** : avatar, "Invité", CTA Se connecter — la découverte reste possible sans compte.
* **Sections** : Découverte (Favoris ← routé, Alertes, Historique, Mes avis), Préférences (Localisation, Compte, Paramètres), bouton Se déconnecter (session V1 TODO).

### Étape 14 : Police Plus Jakarta Sans (famille XML + res/font)

* Police **[Plus Jakarta Sans](https://fonts.google.com/specimen/Plus+Jakarta+Sans)** (OFL-1.1) ajoutée à l'application, remplaçant Roboto (FontFamily.Default).
* Les gravées statiques sont récupérées depuis `C:\Users\Jadj\Downloads\Plus_Jakarta_Sans\static` et copiées dans `app/src/main/res/font/` : Regular, Medium, SemiBold, Bold, ExtraBold, Light, ainsi que leurs variantes Italic.
* Création d'une **famille de polices en XML** : `res/font/togo_font_family.xml` (`<font-family>` avec 4 `<font>` — 400 normal, 600 normal, 700 normal, 400 italic).
* `Type.kt` : `PlusJakartaSans` devient `FontFamily(Font(R.font.togo_font_family))` ; les styles `Typography` (bodyLarge, bodyMedium, bodySmall, titleLarge, titleMedium, titleSmall, labelLarge, labelMedium, labelSmall) conservent leur `FontWeight` (Normal / SemiBold / Medium) et utilisent désormais `PlusJakartaSans`.
* Validation : `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL (aapt2 valide le XML et les 11 polices). `aapt2 dump resources` : `font/togo_font_family` (type XML) et 11 fichiers `font/` packagés dans `app-debug.apk`. APK installé sur l'émulateur Android (avd Medium_Phone) et `am start` réussi : le processus de l'application est vivant (pas de crash au lancement). Vérification graphique du rendu à faire sur l'écran d'accueil.

### Étape 15 : Corrections audit UI/UX (P0 → P2)

* **P0 Search** : `MenuItemRow` (paramètre dupliqué retiré) + suppression des `TODO()` crashants ; `onSellerClick` câblé dans `MainScreen`.
* **P0 Préparation** : intents Appeler / WhatsApp / Maps / Partager restaurés ; skeletons Loading/Error ; UI Custom Request (`RequestModeSwitch`, chips, éditeur message).
* **P1 Auth invité** : `startDestination = MAIN` ; Profil → Login câblé ; Register étape vendeur 2 passe par OTP (`submitStep`) ; RoleSelector masqué à l'étape 2.
* **P1 Layouts** : Home en `LazyColumn` ; Favoris `fillMaxWidth()` ; Map pan en `IntOffset` (px) ; Seller `openSellerMaps` + bouton Info mort retiré.
* **P2 Polish** : dead ends masqués (social login, MDP oublié, déconnexion invité, lignes Profil non câblées) ; insets `statusBars` Search/Profil/Favoris/Map ; strings + a11y favoris/carte.

### Étape 16 : Refonte SellerDetail Home-first + RequestOptions par catégorie

* **RequestOptions** : `groupsFor(prep)` branche sur `categoryId` — sauce/riz (`riz`/`pate`/`fufu`/`poulet`), brochettes, snacks, jus/boissons ; `emptyList()` masque la section Custom Request.
* **SellerCard (PrepDetail)** : badge « Vendeur vérifié » uniquement si `seller.isVerified` ; icône Material à la place de l'emoji.
* **SellerDetailScreen** aligné Home : hero pastel coins bas arrondis, barre Retour/Favori/Partager, identité alignée gauche (ExtraBold + chips Pro/note/Ouvert), logistics `#F5F5F5` sans bordure (Itinéraire + Appeler), menu = grille 2 col. `PreparationCard`, blocs À propos + Populaire, constantes couleurs/typo Home.
* **FavoritesRepository** : `sellerIds` + `toggleSeller` pour le favori vendeur.
* Strings SellerDetail / Prep déplacées dans `strings.xml`.

### Étape 17 : Motion system (fluidité)

* **`TogoMotion`** (`ui/theme/Motion.kt`) : durées, springs, transitions tabs/détail/sheet + `Modifier.togoEnter()`.
* **Nav** : tabs en fade+scale léger ; détail vendeur/plat en slide horizontal ; map en sheet depuis le bas ; bottom bar animée (slide).
* **Contenu** : Home / Search / Favoris via `contentSwitch()` ; Custom Request `animateContentSize` ; cartes press/cœur via springs partagés.

### Étape 18 : Animations globales + tabs SellerDetail

* **Alignement TogoMotion** sur tous les écrans (Home, Search, Favoris, Profil, Map, Auth, PrepDetail, SellerDetail) + chips Home / boutons.
* **`UnderlineTabs`** extrait dans `ui/components/` (indicateur orange animé + badges) — réutilisé par Search (Préparations / Vendeurs).
* **SellerDetail** : sections Menu + Populaire remplacées par 2 onglets (`Menu` / `Populaire`) avec grille + `contentSwitch()` au changement d’onglet.

### Étape 19 : Expérience vendeur Phase 1 (PRD §35)

* **`SessionRepository` + `UserSession`** : rôle CLIENT/VENDEUR en mémoire ; Register crée la boutique ; Login mock rattache un vendeur si téléphone mock connu.
* **`CatalogRepository`** : catalogue mutable partagé (vendeurs + préparations) — source unique pour discovery et hub.
* **Repos Profil → Ma boutique** (`SellerHubScreen`) : toggle Ouvert/Fermé (`VendorStatus` + `SellerOpenStatus`) + toggles dispo par préparation.
* Home / Search / Map / SellerDetail / PrepDetail / Favoris observent le même état (fermeture boutique / indispo se reflètent côté client).

### Étape 20 : Expérience vendeur complète (PRD §35 / §36 / §37)

* **Hub enrichi** : CTA profil / horaires / ajout prep, empty state, rangées cliquables (édition), lien « Voir ma page publique » → `seller_detail/{id}`.
* **Édition profil** (`SellerProfileEditScreen`) : nom, description, téléphone (8 chiffres), zone, repère → `CatalogRepository.updateSellerProfile` (sync `sellerName` / `sellerZone` sur les plats).
* **CRUD préparations** (`SellerPrepEditScreen`) : create/edit/delete, catégories mock, populaire, dispo Modèle A, variantes label+prix FCFA (min. 1, prix > 0), photo = emoji catégorie.
* **Horaires** : modèle `DaySchedule` / `TimeSlot` sur `Seller` + `SellerScheduleScreen` (7 jours, 1–2 créneaux) ; toggle hub = override manuel (`useSchedule = false`) ; save planning active le mode horaire et recalcule `SellerOpenStatus`.
* **SellerDetail** : bloc « Horaires » résumé (`SellerScheduleResolver.summaryLines`).
* Routes : `seller_profile_edit`, `seller_prep_edit?prepId=…`, `seller_schedule`. Hors scope : photo upload, stock, panier, stats.
---

### Étape 21 : Onboarding vendeur complet + Hub Ma boutique (UI)

* **Onboarding vendeur 5 étapes** (plus le banal 1→2) :
  1. Compte (nom + WhatsApp pro) → OTP
  2. Boutique (nom, description ≥10 car., spécialité catégorie)
  3. Localisation (zone, repère, indice « comment me trouver »)
  4. Horaires (presets + jours + créneau, ≥1 jour ouvert)
  5. Récapitulatif + acceptation CGU vendeur → création boutique via `CatalogRepository.createSellerFromRegistration`
* UI dédiée : `VendorOnboardingSections.kt` (progress bar, tip cards, chips jours/presets, review blocks).
* **Ma boutique (`SellerHubScreen`)** refondu : hero pastel, identité + localisation, toggle Ouvert/Fermé, stats (plats / dispo / avis), bloc À propos, raccourcis tuiles, grille **`PreparationCard`** (sans favori) + switch « En vente » sous chaque carte.
* Strings onboarding + hub enrichies dans `strings.xml`.

### Étape 22 : Polish onboarding vendeur (motion + carrousels)

* **`MotivAutoCarousel`** : pager auto (≈3s), scale léger, dots animés — utilisé sur les 5 étapes.
* **Étape récap** : hero aperçu public (pulse 🚀), carrousel « Et après ? », cartes résumé staggered (`togoEnter`), bandeau prêt, checkbox CGU animée.
* Progress bar avec fill gradient animé ; chips catégorie / jours / heures avec transitions couleur.
* Titres / slides motivation dans `strings.xml`.

---

### Étape 23 : Favoris — Assiettes / Spots + filtres street-food

* **Tabs non génériques** : `Assiettes` / `Spots` via `UnderlineTabs` (réutilise le design Search), plutôt que Food/Restaurants.
* **Filtres mood** sur Assiettes : `Tout` · `Chaud maintenant` (AVAILABLE) · `À deux pas` (≤ 800 m).
* **Spots** : liste des vendeurs favoris (`FavoritesRepository.favoriteSellers`) avec statut Ouvert/Fermé + retrait cœur.
* **Header** : « Mes coups de cœur », sous-titre Lomé, résumé compteurs (+ spots ouverts).
* **Repo** : résolution des favoris via `CatalogRepository` (sync hub vendeur).
* Empty states distincts (global / onglet / filtre) + navigation Spot → `seller_detail`.

---

### Étape 24 : Search — hiérarchie visuelle des résultats

* **`MenuItemRow`** enrichi : titre ExtraBold → vendeur/zone → description → note+distance → **prix vert** (`#2E7D32`) + `AvailabilityBadge` ; image pastel catégorie.
* **`SellerResultRow`** : nom + vérifié, description, zone/repère/distance, note, badge Ouvert/Fermé via `SellerOpenStatus` (plus `VendorStatus`).
* Strings Search dédiées (`search_prep_seller_meta`, `search_price_*`, `search_seller_location`).

---

### Étape 25 : Skill Senior UX/UI

* Skill agent `.agents/skills/senior-ux-ui/` (+ miroir `~/.cursor/skills/senior-ux-ui/`) : ladder junior→staff (listes/cards + filtres), checklist anti-junior, playbook Compose TogoFood.
* Déclencher pour polish UI, hiérarchie, filtres, Search/Home/Favoris.

---

### Étape 26 : Search staff (skill senior-ux-ui)

* **Job** : négocier avec le marché Lomé (plat / vendeur) — feedback avant apply.
* **Chips rapides** : counts live `Ouvert · N` / `Populaire · N` / `À - de 1 km · N` (marché hors chip sélectionné).
* **Sheet unique** (`SearchFiltersSheet`) : budget max (chips ≤1000…5000 F) + note min (4,0+ / 4,5+) + CTA `Voir N résultats` (preview draft).
* Placeholder précis (`Riz, fufu, Tokoin, vendeur…`) ; strings Search externalisées ; filtre Ouvert aligné `SellerOpenStatus`.

---

### Étape 27 : Home senior (audit skill senior-ux-ui)

* **Chrome réduit** : suppression des `HomeMode` redondants ; 1 rangée catégories calmes + 1 rangée filtres.
* **Filtres décisionnels** : counts `· N` ; sheet Prix câblée (CTA `Voir N`) ; Ouvert = `SellerOpenStatus`.
* **Appétit** : `PreparationCard` 188×140, hiérarchie nom→vendeur→note→prix vert+distance ; promo plat-first + prix `#2E7D32`.
* **Résultats** : grille 2 colonnes (plus LazyRow seul) + compteur ; shape language radius 14 ; placeholder Home aligné Search.

---

### Étape 28 : Ma boutique senior (skill senior-ux-ui)

* **Job** : décider vite Ouvert/Fermé + quels plats sont en vente.
* **Ancre** : toggle statut en premier ; identité (emoji catégorie dominante + nom) sous le média.
* **Catalogue** : chips `Tout · N` / `En vente · N` / `En pause · N` ; cartes indispo atténuées ; switch « En vente/En pause ».
* Chrome réduit : bloc À propos/tuiles 2×2 remplacés par description 2 lignes + chips actions (Profil / Horaires / Page publique) ; radius 14 cohérent.

### Étape 29 : Ma boutique staff (suite audit senior-ux-ui)

* **Alerte décisionnelle** : banner si Ouvert + 0 plats en vente → CTA filtre « En pause · N ».
* **Contrôle layout** : défaut Liste (toggles rapides) + bascule Cartes ; prix vert dans la ligne.
* **Chrome** : stats Plats/Dispo/Avis retirées (counts déjà dans chips) ; cards hub sans badge Ouvert/nom vendeur redondants.

### Étape 30 : PreparationCard + BottomNav (skill senior-ux-ui)

* **PreparationCard** : média 160 dp, emoji placeholder calme ; statut Ouvert/indispo **sous** le média (Ouvert masqué) ; prix vert ExtraBold sur sa ligne ; note+distance muted.
* **BottomNav** : labels Découvrir/Chercher/Favoris/Compte (`strings.xml`) ; icônes outline→filled ; badge favoris N ; pastille vendeur sur Compte ; hairline + surface `#FAFAFA`.

---

## 🚀 Prochaines étapes (À faire)
* Intégration Firebase / DataStore session / vraie carte
* Upload photo préparations / vendeur
* Stats vendeur & réponses aux avis

