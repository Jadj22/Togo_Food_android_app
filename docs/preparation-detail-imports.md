# Page Détail Préparation — Dépendances importées cachées

> Inventaire des `import` de `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:1-50` qui ne sont pas visibles directement à l'écran mais qui devront être ouverts/modifiés lors du plan d'amélioration 01→14.

Objectif du plan : passer de la page actuelle à une page `Qu'est-ce que c'est ? → Combien ? → Disponible ? → Où ? → Chez qui ? → Est-ce fiable ? → Que puis-je faire ?`

---

## 1. Dépendances métier cachées (à prendre en compte)

| Import `PreparationDetailScreen.kt:line` | Fichier source réel | Nature | Impact direct sur le plan 01→14 |
|---|---|---|---|
| `viewModel()` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:43,54` + type `PreparationDetailViewModel` (même package, pas d'import) | `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailViewModel.kt:1` | `ViewModel` avec `SavedStateHandle["preparationId"]` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailViewModel.kt:18` + `MockPreparationRepository().getById()` synchrone `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailViewModel.kt:16-28` | **11 États de page, 12 Réseau/données anciennes** : actuel `if(prep==null) Text("Chargement...")` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:61` insuffisant. À réécrire en `StateFlow<UiState>` avec `isLoading`/`error`/`notFound`/`lastUpdated`. Doit gérer `null` pour **Préparation inexistante**. |
| `FavoritesRepository` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:44` | `app/src/main/java/com/example/togofood/data/repository/FavoritesRepository.kt:16` | `object` singleton `StateFlow<Set<String>>` en mémoire seule `app/src/main/java/com/example/togofood/data/repository/FavoritesRepository.kt:20-23` + `toggle()` `app/src/main/java/com/example/togofood/data/repository/FavoritesRepository.kt:27` | **13 Favoris** : utilisé via `ids.collectAsState()` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:59` et `toggle(prep.uuid)` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:81`. Animation cœur, persistance DataStore future, feedback à toucher ici. |
| `MockSellerRepository` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:45` | `app/src/main/java/com/example/togofood/data/repository/SellerRepository.kt:11` | `class MockSellerRepository : SellerRepository` `app/src/main/java/com/example/togofood/data/repository/SellerRepository.kt:11-14` basé sur `MockData.sellers` | **05 Distance + localisation rapide, 06 Carte vendeur, 07 Où trouver** : instancié à chaque recompo `remember(prep.sellerId){ MockSellerRepository().getById() }` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:72` (anti-pattern). À injecter via VM/DI. Fournit `phone`/`landmark`/`isVerified`/`vendorStatus` utilisés en `PrepLogisticsCard` et `SellerCard`. |
| `AvailabilityStatus` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:46` | `app/src/main/java/com/example/togofood/domain/model/AvailabilityStatus.kt:5` | `enum AVAILABLE/UNAVAILABLE/TO_CHECK/CLOSES_SOON/UNKNOWN` avec `label`/`emoji`/`color` `app/src/main/java/com/example/togofood/domain/model/AvailabilityStatus.kt:10-14` | **04 Disponibilité réelle** : `when` actuel `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:169-174` collapse `TO_CHECK`+`UNKNOWN` en `else "Disponibilité à vérifier"`. À différencier `UNKNOWN → ⚪ #9E9E9E` et `TO_CHECK → 🟡 #FF9800`. Couleur pastille `prep.availabilityStatus.color` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:179`. |
| `Preparation` (FQN `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:135`) | `app/src/main/java/com/example/togofood/domain/model/Preparation.kt:3` | `data class Preparation(uuid,name,description,photoUrl,categoryId,sellerId,sellerName,sellerZone,availabilityStatus,variants,rating,reviewCount,distanceMeters)` `app/src/main/java/com/example/togofood/domain/model/Preparation.kt:3-18` + `basePrice` `app/src/main/java/com/example/togofood/domain/model/Preparation.kt:20` | **01 Hero, 02 Identité, 03 Prix/variantes, 08 Description** : `photoUrl:7` existe mais jamais utilisé (hero utilise `categoryId` + `FoodIcons`). Dans `app/src/main/java/com/example/togofood/data/mock/MockData.kt:58` `photoUrl=""` partout → prévoir `Coil AsyncImage` + placeholder. `basePrice` = `minOf` variants pour **03 À partir de**. |
| `Seller` (FQN `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:166`) | `app/src/main/java/com/example/togofood/domain/model/Seller.kt:3` | `data class Seller(uuid,name,description,phone,photoUrl,vendorStatus,zone,landmark,rating,reviewCount,isVerified,distanceMeters)` `app/src/main/java/com/example/togofood/domain/model/Seller.kt:3-16` | **06 Carte vendeur, 07 Où trouver** : `isVerified` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:214`, `landmark` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:188`, `vendorStatus` non affiché (confusion `Disponible` préparation vs `Ouvert` vendeur à corriger). |
| `Variant` (FQN `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:288`) | `app/src/main/java/com/example/togofood/domain/model/Variant.kt:3` | `data class Variant(label,priceFcfa)` `app/src/main/java/com/example/togofood/domain/model/Variant.kt:3-6` | **03 Prix & variantes** : liste `prep.variants` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:108` affichée en `PriceRow`. Pas de champ `isAvailable`/`isDefault` → à ajouter pour variante désactivée/sélection visuelle. |
| `FoodIcons` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:48` | `app/src/main/java/com/example/togofood/ui/components/FoodIcons.kt:9` | `object FoodIcons` `emojiFor(categoryId)` `app/src/main/java/com/example/togofood/ui/components/FoodIcons.kt:11-21` + `pastelBackgrounds` `app/src/main/java/com/example/togofood/ui/components/FoodIcons.kt:23-32` | **01 Hero** : `bgColor = pastelBackgrounds[categoryId]` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:141` + `emojiFor` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:144`. À remplacer par vraie image `photoUrl` avec fallback pastel/emoji. Mapping limité à 8 catégories. |
| `BrandOrange` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:47` | `app/src/main/java/com/example/togofood/ui/components/Buttons.kt:31` | `val BrandOrange = Color(0xFFE65100)` `app/src/main/java/com/example/togofood/ui/components/Buttons.kt:31` | **Design system global** : utilisé 4× `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:88,127,183,215`. Single source of truth couleur primaire. |
| `formatDistance` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:49` | `app/src/main/java/com/example/togofood/ui/components/FormatUtils.kt:8` | `fun Int.formatDistance(): String` `app/src/main/java/com/example/togofood/ui/components/FormatUtils.kt:8-9` (`<1000 → "380 m"` sinon `"1.1 km"`) | **05 Distance** : `val dist = prep.distanceMeters.formatDistance()` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:69` affiché `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:189`. |
| `formatFcfa` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:50` | `app/src/main/java/com/example/togofood/ui/components/FormatUtils.kt:4` | `fun Int.formatFcfa(): String` `app/src/main/java/com/example/togofood/ui/components/FormatUtils.kt:4-5` (`"2 000"` via `Locale.US`) | **03 Prix** : `PriceRow(v.label, "${v.priceFcfa.formatFcfa()} FCFA")` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:109` + `prep.basePrice` ailleurs. |
| `LocalContext` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:39` | Framework `androidx.compose.ui.platform.LocalContext` | `val ctx = LocalContext.current` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:70` | **07 Itinéraire, 10 Actions, 14 Partage** : contexte passé à `sharePrep`/`openMaps`/`openWhatsApp`/`dialPhone`. |
| `android.content.Context/Intent/Uri/Toast` (FQN, pas d'import) | Framework Android | `dialPhone` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:248-256`, `openMaps` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:258-270`, `sharePrep` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:272-282`, `openWhatsApp` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:284-301` | **10 Actions finales** : `sharePrep` actuel `EXTRA_TEXT="Regarde ce plat : $name"` à enrichir `Nom — prix · Chez vendeur · Zone`. `openWhatsApp` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:284` sera **supprimé** (remplacé par `Voir le vendeur` selon plan). `openMaps` avec `geo:0,0?q=...Lomé` à conserver. |

---

## 2. Dépendances transitives (non importées mais impactées)

| Fichier | Pourquoi l'ouvrir |
|---|---|
| `app/src/main/java/com/example/togofood/data/mock/MockData.kt:56` | Source de vérité `preparations` (10 items) et `sellers` (5). `photoUrl=""` partout explique le placeholder emoji actuel. Toute vraie photo passe par ici (ou API future). |
| `app/src/main/java/com/example/togofood/data/repository/MockPreparationRepository.kt` | Utilisé par `PreparationDetailViewModel.kt:16` et `FavoritesRepository.kt:18`. Si VM passe en `Flow`/`Result`, ce repo change. |
| `app/src/main/java/com/example/togofood/domain/model/VendorStatus.kt` (`app/src/main/java/com/example/togofood/domain/model/Seller.kt:18`) | `ACTIVE/CLOSED/SUSPENDED` — nécessaire pour **06 `🟢 Ouvert maintenant` vs 🔴 Fermé** (actuellement non affiché dans `SellerCard`). |
| `app/src/main/java/com/example/togofood/ui/components/Buttons.kt:31` | Définit `BrandOrange` + `PrimaryButton` qui remplacera `OutlinedButton Commander sur WhatsApp` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:123` par `Voir le vendeur`. |

---

## 3. Imports présents mais non utilisés (à nettoyer)

| Import `PreparationDetailScreen.kt:line` | Statut |
|---|---|
| `androidx.compose.material.icons.filled.Call` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:17` | Inutilisé — redeviendra utile pour **14 📞 Appeler** si bouton d'appel ajouté |
| `androidx.compose.material.icons.filled.Check` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:18` | Inutilisé |
| `androidx.compose.material.icons.filled.LocationOn` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:21` | Inutilisé — utile pour **05 📍 distance** |
| `androidx.compose.material3.Button` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:24` | Inutilisé (seul `OutlinedButton` utilisé) |
| `androidx.compose.material3.ButtonDefaults` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:25` | Inutilisé |
| `androidx.compose.runtime.mutableStateOf` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:32` | Inutilisé |
| `androidx.compose.runtime.setValue` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:34` | Inutilisé (`getValue` seul utilisé pour `by`) |
| `remember` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:33` | Utilisé 1× `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:72` — conserver |

---

## 4. Matrice d'impact par bloc du plan

| Bloc plan | Fichiers à modifier en plus de `PreparationDetailScreen.kt` |
|---|---|
| **01 Hero / Photo 300dp + badge dispo** | `domain/model/Preparation.kt:7 photoUrl`, `data/mock/MockData.kt:58`, `ui/components/FoodIcons.kt:9`, `domain/model/AvailabilityStatus.kt:5` (badge 🟢/🔴/🟠/⚪ sur image) |
| **02 Identité + avis** | `domain/model/Preparation.kt:14-15 rating/reviewCount` — pas de changement model, juste typo `24sp ExtraBold` |
| **03 Prix & variantes (sélection, désactivée, À partir de)** | `domain/model/Variant.kt:3`, `domain/model/Preparation.kt:20 basePrice`, `ui/components/FormatUtils.kt:4` |
| **04 Disponibilité réelle** | `domain/model/AvailabilityStatus.kt:5` |
| **05 Distance + localisation rapide** | `ui/components/FormatUtils.kt:8`, `domain/model/Seller.kt:11 landmark` |
| **06 Carte vendeur cliquable** | `domain/model/Seller.kt:9-14`, `data/repository/SellerRepository.kt:11` |
| **07 Où trouver + mini-carte** | `domain/model/Seller.kt:10-12 zone/landmark`, `PreparationDetailScreen.kt:258 openMaps` |
| **08 Description / Ingrédients** | `domain/model/Preparation.kt:6 description` — pas d'ingrédients dans le model actuel, à ne pas inventer |
| **09 Avis & confiance** | `domain/model/Preparation.kt:14-15` seul dispo ; besoin futur `domain/model/Review.kt` inexistant |
| **10 Actions Voir le vendeur (supprimer WhatsApp)** | `ui/components/Buttons.kt:31 PrimaryButton`, supprimer `PreparationDetailScreen.kt:284 openWhatsApp` + `OutlinedButton` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:123` |
| **11 États Loading/Error/Empty** | `ui/screens/preparation/PreparationDetailViewModel.kt:1` (refacto complet) |
| **12 Données anciennes** | `PreparationDetailViewModel.kt:1` + futur `lastUpdated` dans `Preparation.kt` |
| **13 Favoris** | `data/repository/FavoritesRepository.kt:16` |
| **14 Partage enrichi** | `PreparationDetailScreen.kt:272 sharePrep` |

---

## 5. Checklist avant de coder

- [ ] Ouvrir `PreparationDetailViewModel.kt:1` et prévoir `UiState(Loading/Success/Error)` au lieu de `MutableStateFlow<Preparation?>`
- [ ] Vérifier `MockData.kt:56` : `photoUrl` vide → implémenter `Coil` avec `ContentScale.Crop` `300dp` + `placeholder FoodIcons` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:142`
- [ ] Ne pas confondre `AvailabilityStatus` (préparation) et `VendorStatus` (vendeur) — 2 pastilles distinctes **04 vs 06**
- [ ] Supprimer `Commander sur WhatsApp` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:122-128` au profit de `Voir le vendeur` `onSellerClick` `app/src/main/java/com/example/togofood/ui/screens/preparation/PreparationDetailScreen.kt:56`
- [ ] Nettoyer imports morts listés en §3
