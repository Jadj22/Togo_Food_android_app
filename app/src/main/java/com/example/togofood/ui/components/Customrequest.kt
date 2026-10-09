package com.example.togofood.ui.components

import com.example.togofood.domain.model.Preparation

/*
 * Demande personnalisée (PRD — « Demande personnalisée »)
 *
 * L'utilisateur précise ce qu'il veut (accompagnement, piment, suppléments, boisson…)
 * puis envoie un message au vendeur. Ce n'est PAS une commande :
 *   - aucun panier, aucun stock, aucun paiement ;
 *   - les options sont gratuites par défaut ; certains suppléments affichent un prix
 *     INDICATIF (`extraPrice`) : c'est une estimation, le vendeur confirme le montant ;
 *   - le vendeur reste libre de répondre « c'est fini », « j'ai du poulet à la place », etc.
 */

/** Une seule option possible dans le groupe, ou plusieurs. */
enum class OptionSelection { SINGLE, MULTIPLE }

/**
 * Une option proposée à l'utilisateur (« Poisson », « Un peu de piment »…).
 *
 * @param phrase     fragment inséré dans la phrase du message (« du poisson »).
 *                   `null` = ne rien écrire (ex. « Aucun », « Normal »).
 * @param isWithout  vrai pour une exclusion (« sans piment ») : placée en fin de phrase.
 * @param isModifier vrai pour un adjectif qui qualifie le plat (« bien cuit », « peu sucré ») :
 *                   placé juste après le plat, sans « avec ».
 * @param extraPrice supplément payant en FCFA. `0` = gratuit ou inclus (rien n'est affiché).
 *                   Prix indicatif : le vendeur reste libre de le modifier.
 */
data class RequestOption(
    val id: String,
    val label: String,
    val phrase: String?,
    val emoji: String? = null,
    val isWithout: Boolean = false,
    val isModifier: Boolean = false,
    val extraPrice: Int = 0
) {
    val isPaid: Boolean get() = extraPrice > 0
}

data class RequestOptionGroup(
    val id: String,
    val title: String,
    val selection: OptionSelection,
    val options: List<RequestOption>
)

/**
 * Source des options proposées pour une préparation.
 *
 * Chacune des 200 préparations de MockData (p1…p200) a ses propres groupes, choisis selon
 * ce que le plat contient réellement : on ne propose pas « Poulet » sur un poulet rôti,
 * pas de « Cuisson » sur une soupe, pas de « Piment » sur un jus, etc.
 *
 * Une préparation absente de la table reçoit [genericGroups].
 * `emptyList()` → la section « Personnaliser » est masquée sur la page détail.
 * Quand l'API exposera de vraies options par plat/vendeur, seul cet objet change.
 */
object RequestOptions {

    fun groupsFor(prep: Preparation): List<RequestOptionGroup> =
        byPreparation[prep.uuid] ?: genericGroups

    // ============================================================
    // CONSTRUCTEURS
    // ============================================================

    private fun o(id: String, label: String, phrase: String?, emoji: String? = null) =
        RequestOption(id = id, label = label, phrase = phrase, emoji = emoji)

    /** Adjectif qui qualifie le plat (« bien cuit »). */
    private fun m(id: String, label: String, phrase: String) =
        RequestOption(id = id, label = label, phrase = phrase, isModifier = true)

    /** Exclusion (« sans oignons »). */
    private fun w(id: String, label: String, phrase: String) =
        RequestOption(id = id, label = label, phrase = phrase, isWithout = true)

    private fun single(id: String, title: String, vararg options: RequestOption) =
        RequestOptionGroup(id, title, OptionSelection.SINGLE, options.toList())

    private fun multi(id: String, title: String, vararg options: RequestOption) =
        RequestOptionGroup(id, title, OptionSelection.MULTIPLE, options.toList())

    // ============================================================
    // OPTIONS PARTAGÉES
    // ============================================================

    private val NONE = o("none", "Aucun", null)

    // Viandes et poissons
    private val CHICKEN = o("chicken", "Poulet", "du poulet", "🍗")
    private val FISH = o("fish", "Poisson", "du poisson", "🐟")
    private val FRIED_FISH = o("fried_fish", "Poisson frit", "du poisson frit", "🐟")
    private val SMOKED_FISH = o("smoked_fish", "Poisson fumé", "du poisson fumé")
    private val BEEF = o("beef", "Bœuf", "du bœuf", "🥩")
    private val MUTTON = o("mutton", "Mouton", "du mouton")
    private val GOAT = o("goat", "Chèvre", "de la chèvre")
    private val PORK = o("pork", "Porc", "du porc")
    private val EGG = o("egg", "Œuf", "un œuf", "🥚")
    private val SARDINES = o("sardines", "Sardines", "des sardines")

    // « Plus de … »
    private val MORE_SAUCE = o("more_sauce", "Plus de sauce", "plus de sauce")
    private val MORE_RICE = o("more_rice", "Plus de riz", "plus de riz")
    private val MORE_CHICKEN = o("more_chicken", "Plus de poulet", "plus de poulet")
    private val MORE_MEAT = o("more_meat", "Plus de viande", "plus de viande")
    private val MORE_SMOKED = o("more_smoked", "Plus de poisson fumé", "plus de poisson fumé")
    private val MORE_FRIED_FISH = o("more_fried_fish", "Plus de poisson frit", "plus de poisson frit")
    private val MORE_ATTIEKE = o("more_attieke", "Plus d'attiéké", "plus d'attiéké")
    private val MORE_ALLOCO = o("more_alloco", "Plus d'alloco", "plus d'alloco")
    private val MORE_GARI = o("more_gari", "Plus de gari", "plus de gari")
    private val MORE_SHRIMP = o("more_shrimp", "Plus de crevettes", "plus de crevettes")
    private val MORE_EGG = o("more_egg", "Œuf en plus", "un œuf en plus")
    private val MORE_GINGER = o("more_ginger", "Plus de gingembre", "plus de gingembre")
    private val MORE_GARLIC = o("more_garlic", "Plus d'ail", "plus d'ail")
    private val MORE_CHEESE = o("more_cheese", "Plus de fromage", "plus de fromage")
    private val MORE_MIL = o("more_mil", "Plus de mil", "plus de mil")
    private val MORE_CURD = o("more_curd", "Plus de lait caillé", "plus de lait caillé")
    private val SAUSAGE_MORE = o("more_sausage", "Saucisse en plus", "une saucisse en plus")

    // Accompagnements
    private val RICE = o("rice", "Riz", "du riz")
    private val PASTE = o("paste", "Pâte de maïs", "de la pâte de maïs")
    private val FUFU = o("fufu", "Foufou", "du foufou")
    private val YAM = o("yam", "Igname", "de l'igname")
    private val ALLOCO = o("alloco", "Alloco", "de l'alloco")
    private val ATTIEKE = o("attieke", "Attiéké", "de l'attiéké")
    private val GARI = o("gari", "Gari", "du gari")
    private val BREAD = o("bread", "Pain", "du pain")
    private val FRIES = o("fries", "Frites", "des frites")
    private val PLANTAIN = o("plantain", "Plantain", "du plantain")
    private val SALAD = o("salad", "Salade", "de la salade")
    private val AVOCADO = o("avocado", "Avocat", "de l'avocat")
    private val PEANUTS = o("peanuts", "Arachides", "des arachides")

    // Fromages, charcuterie, sauces
    private val CHEESE = o("cheese", "Fromage", "du fromage")
    private val GRATED_CHEESE = o("grated", "Fromage râpé", "du fromage râpé")
    private val SAUSAGE = o("sausage", "Saucisse", "une saucisse")
    private val TOMATO_SAUCE = o("tomato_sauce", "Sauce tomate", "de la sauce tomate")
    private val CHEESE_SAUCE = o("cheese_sauce", "Sauce fromagère", "de la sauce fromagère")
    private val KETCHUP = o("ketchup", "Ketchup", "du ketchup")
    private val MAYO = o("mayo", "Mayonnaise", "de la mayonnaise")
    private val MUSTARD = o("mustard", "Moutarde", "de la moutarde")
    private val HOT_SAUCE = o("hot_sauce", "Sauce piquante", "de la sauce piquante")
    private val SOY = o("soy", "Sauce soja", "de la sauce soja")

    // Condiments et « à part »
    private val ONIONS = o("onions", "Oignons", "des oignons")
    private val TOMATOES = o("tomatoes", "Tomates", "des tomates")
    private val LEMON = o("lemon", "Citron", "du citron")
    private val MINT = o("mint", "Menthe", "de la menthe")
    private val GINGER = o("ginger", "Gingembre", "du gingembre")
    private val HONEY = o("honey", "Miel", "du miel")
    private val PEPPER_APART = o("pepper_apart", "Piment à part", "du piment à part")
    private val HOT_SAUCE_APART = o("hot_sauce_apart", "Sauce piquante à part", "de la sauce piquante à part")
    private val SALT_APART = o("salt_apart", "Sel à part", "du sel à part")
    private val SUGAR_APART = o("sugar_apart", "Sucre à part", "du sucre à part")

    // Douceurs
    private val SUGAR_TOP = o("sugar_top", "Sucre", "du sucre")
    private val CHOCOLATE = o("chocolate", "Chocolat", "du chocolat")
    private val BANANA = o("banana", "Banane", "de la banane")
    private val YOGURT = o("yogurt", "Yaourt", "du yaourt")
    private val COCONUT = o("coconut", "Noix de coco", "de la noix de coco")
    private val VANILLA_ADD = o("vanilla_add", "Vanille", "de la vanille")
    private val MILK = o("milk", "Lait", "du lait")
    private val ICE_CUBES = o("ice_cubes", "Glaçons", "des glaçons")

    // Gâteaux
    private val CANDLES = o("candles", "Bougies", "des bougies")
    private val MESSAGE = o("message", "Message écrit dessus", "un message écrit dessus")
    private val GIFT = o("gift", "Emballage cadeau", "un emballage cadeau")
    private val DECOR = o("decor", "Décoration spéciale", "une décoration spéciale")

    // Pizza
    private val PIZZA_CHEESE = o("pizza_cheese", "Fromage supplémentaire", "du fromage supplémentaire")
    private val OLIVES = o("olives", "Olives", "des olives")
    private val MINCED = o("minced", "Viande hachée", "de la viande hachée")

    // Exclusions
    private val NO_ONION = w("no_onion", "Sans oignons", "sans oignons")
    private val NO_TOMATO = w("no_tomato", "Sans tomate", "sans tomate")
    private val NO_SALAD = w("no_salad", "Sans salade", "sans salade")
    private val NO_CUCUMBER = w("no_cucumber", "Sans concombre", "sans concombre")
    private val NO_EGG = w("no_egg", "Sans œuf", "sans œuf")
    private val NO_FISH = w("no_fish", "Sans poisson", "sans poisson")
    private val NO_GARI = w("no_gari", "Sans gari", "sans gari")
    private val NO_PEPPERS = w("no_peppers", "Sans poivrons", "sans poivrons")
    private val NO_OLIVES = w("no_olives", "Sans olives", "sans olives")
    private val NO_CARROT = w("no_carrot", "Sans carottes", "sans carottes")
    private val NO_CABBAGE = w("no_cabbage", "Sans choux", "sans choux")
    private val NO_PEAS = w("no_peas", "Sans petits pois", "sans petits pois")
    private val NO_HAM = w("no_ham", "Sans jambon", "sans jambon")
    private val NO_CORN = w("no_corn", "Sans maïs", "sans maïs")
    private val NO_BASIL = w("no_basil", "Sans basilic", "sans basilic")
    private val NO_FRIES = w("no_fries", "Sans frites", "sans frites")
    private val NO_BANANA = w("no_banana", "Sans banane", "sans banane")
    private val NO_PAPAYA = w("no_papaya", "Sans papaye", "sans papaye")

    // ============================================================
    // GROUPES RÉUTILISABLES
    // ============================================================

    private fun spice() = single(
        "spice", "Piment",
        w("no_spice", "Sans piment", "sans piment"),
        o("mild_spice", "Un peu", "un peu de piment", "🌶️"),
        o("normal_spice", "Normal", "du piment", "🌶️🌶️"),
        o("hot_spice", "Beaucoup", "beaucoup de piment", "🌶️🌶️🌶️")
    )

    /** Force d'un piment pilé ou d'un jus de gingembre. */
    private fun strength() = single(
        "strength", "Force",
        m("mild_strength", "Doux", "doux"),
        m("medium_strength", "Moyen", "moyennement fort"),
        m("strong_strength", "Très fort", "très fort")
    )

    private fun side(vararg options: RequestOption) = single("side", "Accompagnement", *options, NONE)

    /**
     * Prix indicatifs (FCFA) des suppléments, par identifiant d'option.
     * Appliqués uniquement aux groupes « Suppléments », « Garnitures », « Ajouts » et
     * « Accompagnements » en plus : les choix de `side()` (le plat « avec poulet ou poisson »)
     * restent inclus, donc gratuits. Une option absente de la table est gratuite.
     * VALEURS DE DÉMO : à remplacer par les vrais prix quand l'API les fournira.
     */
    private val EXTRA_PRICES: Map<String, Int> = mapOf(
        // Protéines
        "egg" to 100, "more_egg" to 100,
        "chicken" to 800, "more_chicken" to 800,
        "beef" to 800, "more_meat" to 700,
        "fried_fish" to 500, "more_fried_fish" to 500,
        "smoked_fish" to 300, "more_smoked" to 300,
        "more_shrimp" to 800, "sardines" to 200,
        // Accompagnements
        "alloco" to 300, "more_alloco" to 300,
        "attieke" to 500, "more_attieke" to 500,
        "fries" to 400, "plantain" to 200, "avocado" to 300,
        "more_rice" to 300, "bread" to 100, "peanuts" to 100,
        // Fromages et charcuterie
        "cheese" to 200, "grated" to 200, "more_cheese" to 200,
        "sausage" to 200, "more_sausage" to 300,
        // Pizza
        "pizza_cheese" to 500, "olives" to 300, "minced" to 600,
        // Douceurs
        "honey" to 100, "yogurt" to 200, "coconut" to 100, "chocolate" to 200,
        // Gâteaux
        "candles" to 100, "message" to 300, "gift" to 500, "decor" to 1000
    )

    /** Renvoie les options avec leur prix indicatif (si elles en ont un). */
    private fun priced(options: Array<out RequestOption>): Array<RequestOption> =
        Array(options.size) { i ->
            val option = options[i]
            option.copy(extraPrice = EXTRA_PRICES[option.id] ?: option.extraPrice)
        }

    private fun extras(vararg options: RequestOption) = multi("extras", "Suppléments", *priced(options))

    private fun remove(vararg options: RequestOption) = multi("remove", "Retirer", *options)

    private fun toppings(vararg options: RequestOption) = multi("toppings", "Garnitures", *priced(options))

    private fun drink() = single(
        "drink", "Boisson",
        o("bissap", "Jus de bissap", "un jus de bissap"),
        o("ginger_juice", "Jus de gingembre", "un jus de gingembre"),
        o("water", "Eau", "de l'eau"),
        o("no_drink", "Aucune", null)
    )

    private fun sugar() = single(
        "sugar", "Sucre",
        w("no_sugar", "Sans sucre", "sans sucre"),
        m("light_sugar", "Peu sucré", "peu sucré"),
        o("normal_sugar", "Normal", null),
        m("sweet_sugar", "Bien sucré", "bien sucré")
    )

    private fun ice() = single(
        "ice", "Fraîcheur",
        m("cold", "Bien frais", "bien frais"),
        o("ice", "Avec glaçons", "des glaçons"),
        w("no_ice", "Sans glaçons", "sans glaçons"),
        o("as_is", "Comme d'habitude", null)
    )

    private fun cold() = single(
        "temp", "Température",
        m("cold_t", "Bien frais", "bien frais"),
        o("ambient", "Température ambiante", null)
    )

    private fun hot() = single(
        "temp", "Température",
        m("hot_t", "Bien chaud", "bien chaud"),
        m("warm_t", "Tiède", "tiède"),
        o("as_is_t", "Comme d'habitude", null)
    )

    /** Grillades : viande, poisson, crustacés. */
    private fun grill() = single(
        "cooking", "Cuisson",
        m("medium", "À point", "à point"),
        m("well", "Bien cuit", "bien cuit"),
        m("charred", "Très grillé", "très grillé")
    )

    private fun steak() = single(
        "cooking", "Cuisson",
        m("medium", "À point", "à point"),
        m("well", "Bien cuit", "bien cuit")
    )

    /** Fritures. */
    private fun crispy() = single(
        "cooking", "Cuisson",
        o("normal_cook", "Normale", null),
        m("crispy", "Bien croustillant", "bien croustillant")
    )

    private fun piece() = single(
        "piece", "Morceau",
        o("thigh", "Cuisse", "une cuisse"),
        o("wing", "Aile", "une aile"),
        o("breast", "Poitrine", "de la poitrine"),
        o("any_piece", "Peu importe", null)
    )

    private fun sauces() = multi("sauces", "Sauces", KETCHUP, MAYO, MUSTARD, HOT_SAUCE)

    private fun tacosSauces() = multi("sauces", "Sauces", CHEESE_SAUCE, HOT_SAUCE, MAYO, KETCHUP)

    private fun brochetteSauce() = single(
        "sauce", "Sauce",
        o("peanut_sauce", "Sauce arachide", "de la sauce arachide"),
        o("chili_sauce", "Sauce piment", "de la sauce piment"),
        o("onion_sauce", "Oignons", "des oignons"),
        o("no_sauce", "Aucune", null)
    )

    /** Pour les pâtes et ablo servis « avec une sauce au choix ». */
    private fun sauceChoice() = single(
        "sauce", "Sauce",
        TOMATO_SAUCE,
        o("peanut_sauce", "Sauce arachide", "de la sauce arachide"),
        o("palm_sauce", "Sauce graine", "de la sauce graine"),
        o("okra_sauce", "Sauce gombo", "de la sauce gombo"),
        o("ademe_sauce", "Sauce adémè", "de la sauce adémè"),
        o("leaf_sauce", "Sauce aux feuilles", "de la sauce aux feuilles"),
        o("daily_sauce", "Sauce du jour", null)
    )

    private fun ripeness(fem: Boolean = false, plural: Boolean = false): RequestOptionGroup {
        val e = (if (fem) "e" else "") + (if (plural) "s" else "")
        return single(
            "ripe", "Maturité",
            m("ripe", "Bien mûr$e", "bien mûr$e"),
            m("ripe_medium", "À point", "à point"),
            m("ripe_firm", "Un peu ferme${if (plural) "s" else ""}", "un peu ferme${if (plural) "s" else ""}")
        )
    }

    private fun cut(fem: Boolean = false, plural: Boolean = false): RequestOptionGroup {
        val e = (if (fem) "e" else "") + (if (plural) "s" else "")
        return single(
            "cut", "Préparation",
            m("cut_whole", "Entier$e", "entier$e"),
            m("cut_peeled", "Épluché$e", "épluché$e"),
            m("cut_pieces", "Coupé$e en morceaux", "coupé$e en morceaux")
        )
    }

    private fun pulp() = single(
        "pulp", "Pulpe",
        o("with_pulp", "Avec pulpe", "de la pulpe"),
        w("no_pulp", "Sans pulpe", "sans pulpe")
    )

    private fun texture() = single(
        "texture", "Texture",
        m("thick", "Bien épais", "bien épais"),
        m("thin", "Plus liquide", "plus liquide")
    )

    private fun salt() = single(
        "salt", "Assaisonnement",
        m("salt_plain", "Nature", "nature"),
        m("salt_light", "Peu salé", "peu salé"),
        m("salt_normal", "Salé", "salé")
    )

    private fun dressing() = single(
        "dressing", "Vinaigrette",
        o("dressing_normal", "Normale", null),
        o("dressing_apart", "À part", "la vinaigrette à part"),
        w("dressing_none", "Sans vinaigrette", "sans vinaigrette")
    )

    private fun oil() = single(
        "oil", "Huile rouge",
        m("little_oil", "Peu d'huile", "peu d'huile rouge"),
        o("normal_oil", "Normal", null),
        m("more_oil", "Bien huilé", "bien huilé")
    )

    private fun soda() = single(
        "flavor", "Parfum",
        o("cola", "Cola", "un cola"),
        o("orange_soda", "Orange", "un soda à l'orange"),
        o("lemon_soda", "Citron", "un soda citron"),
        o("any_soda", "Peu importe", null)
    )

    private fun popcornTaste() = single(
        "taste", "Goût",
        m("salty", "Salé", "salé"),
        m("sweet_taste", "Sucré", "sucré"),
        m("mixed", "Mélangé", "mélangé")
    )

    private fun garnish() =
        multi("garnish", "Accompagnements", *priced(arrayOf(SARDINES, TOMATO_SAUCE, FRIED_FISH, EGG)))

    private fun fruitSaladExtras() =
        multi("fruit_extras", "Ajouts", *priced(arrayOf(HONEY, LEMON, YOGURT, COCONUT)))

    private fun cakeExtras() = extras(CANDLES, MESSAGE, GIFT)

    private fun pizzaCooking() = single(
        "cooking", "Cuisson",
        o("pizza_normal", "Normale", null),
        m("pizza_well", "Bien cuite", "bien cuite"),
        m("pizza_crispy", "Pâte croustillante", "à la pâte croustillante")
    )

    /** Plat en sauce servi avec riz, pâte, foufou ou igname. */
    private fun sauceDish(vararg extraOptions: RequestOption) =
        listOf(side(RICE, PASTE, FUFU, YAM), spice(), extras(*extraOptions))

    /** Filet de sécurité pour une préparation absente de la table. */
    private val genericGroups = listOf(spice(), extras(MORE_SAUCE, EGG), drink())

    // ============================================================
    // OPTIONS PAR PRÉPARATION (uuid « p1 » … « p200 »)
    // ============================================================

    private val byPreparation: Map<String, List<RequestOptionGroup>> = mapOf(

        // ---------- s1 : Chez Mama Adjoa ----------
        "p1" to listOf(spice(), extras(EGG, FRIED_FISH, GARI, ALLOCO, AVOCADO), remove(NO_ONION), drink()),
        "p2" to listOf(side(CHICKEN, FISH, EGG), spice(), extras(MORE_SAUCE, MORE_RICE, ALLOCO), remove(NO_ONION), drink()),
        "p9" to listOf(spice(), extras(EGG, FRIED_FISH, CHICKEN, ONIONS), drink()),
        "p11" to listOf(side(CHICKEN, FISH, BEEF), spice(), extras(MORE_RICE, ALLOCO, SALAD), remove(NO_ONION), drink()),
        "p12" to listOf(spice(), extras(EGG, FRIED_FISH, ALLOCO, MORE_GARI), remove(NO_ONION)),
        "p13" to listOf(spice(), extras(EGG, SMOKED_FISH, FRIED_FISH, AVOCADO), remove(NO_ONION, NO_TOMATO)),
        "p14" to listOf(spice(), extras(EGG, CHICKEN, MORE_SAUCE, CHEESE), remove(NO_ONION)),
        "p15" to listOf(spice(), sauces(), extras(EGG, FRIED_FISH, CHICKEN)),
        "p16" to listOf(spice(), extras(MORE_SAUCE, MORE_MEAT, MORE_RICE, PLANTAIN), drink()),
        "p17" to listOf(spice(), extras(BREAD, SALT_APART), remove(NO_ONION)),
        "p18" to listOf(ripeness(), spice(), extras(PEANUTS, FRIED_FISH, EGG)),
        "p19" to listOf(crispy(), spice(), extras(MORE_RICE, ALLOCO, FRIES, SALAD), drink()),
        "p20" to listOf(spice(), extras(EGG, CHEESE, MORE_SAUCE), remove(NO_ONION)),

        // ---------- s2 : Kossivi Jus Frais ----------
        "p3" to listOf(sugar(), ice(), extras(MINT, LEMON)),
        "p4" to listOf(strength(), sugar(), ice(), extras(LEMON, HONEY)),
        "p10" to listOf(sugar(), ice()),
        "p21" to listOf(sugar(), ice(), extras(MINT)),
        "p22" to listOf(texture(), sugar(), ice()),
        "p23" to listOf(sugar(), ice(), extras(GINGER, MINT)),
        "p24" to listOf(texture(), sugar(), ice(), extras(LEMON)),
        "p25" to listOf(pulp(), sugar(), ice()),
        "p26" to listOf(sugar(), cold(), extras(MORE_MIL, MORE_CURD)),
        "p27" to listOf(sugar(), cold(), extras(HONEY, SUGAR_APART)),
        "p28" to listOf(cold()),

        // ---------- s3 : Brochettes de Yaovi ----------
        "p5" to listOf(grill(), spice(), brochetteSauce(), extras(BREAD, ALLOCO, TOMATOES), drink()),
        "p29" to listOf(grill(), spice(), brochetteSauce(), extras(BREAD, ALLOCO, TOMATOES), drink()),
        "p30" to listOf(grill(), spice(), extras(ONIONS, TOMATOES, BREAD, ALLOCO), remove(NO_ONION), drink()),
        "p31" to listOf(grill(), spice(), extras(ONIONS, TOMATOES, ALLOCO, ATTIEKE, BREAD), drink()),
        "p32" to listOf(grill(), spice(), extras(ONIONS, TOMATOES, ALLOCO, ATTIEKE, BREAD), drink()),
        "p33" to listOf(grill(), spice(), extras(ONIONS, ALLOCO, ATTIEKE, BREAD), drink()),
        "p34" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, FRIES, RICE), extras(ONIONS, BREAD), drink()),
        "p35" to listOf(spice(), remove(NO_ONION, NO_TOMATO), extras(BREAD, CHEESE)),
        "p36" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO), extras(CHEESE, EGG), drink()),

        // ---------- s4 : Chez Akossiwa ----------
        "p6" to listOf(spice(), extras(MORE_SMOKED, BEEF, CHICKEN, MORE_SAUCE), drink()),
        "p7" to listOf(spice(), extras(SMOKED_FISH, BEEF, MORE_SAUCE), drink()),
        "p37" to listOf(spice(), extras(SMOKED_FISH, BEEF, MORE_SAUCE), drink()),
        "p38" to listOf(spice(), extras(CHICKEN, SMOKED_FISH, MORE_SAUCE), drink()),
        "p39" to listOf(side(FRIED_FISH, CHICKEN, EGG), spice(), extras(MORE_SAUCE), drink()),
        "p40" to listOf(sauceChoice(), spice(), extras(FRIED_FISH, CHICKEN, EGG)),
        "p41" to listOf(sauceChoice(), spice(), extras(SMOKED_FISH, BEEF, CHICKEN)),
        "p42" to listOf(spice(), extras(SMOKED_FISH, CHICKEN, MORE_SAUCE), drink()),
        "p43" to listOf(piece(), side(RICE, ATTIEKE, ALLOCO, PASTE), spice(), extras(MORE_SAUCE), remove(NO_ONION), drink()),
        "p44" to listOf(side(RICE, PASTE, YAM, FUFU), spice(), extras(SMOKED_FISH, BEEF, MORE_SAUCE), drink()),
        "p45" to listOf(side(RICE, PASTE, YAM, FUFU), spice(), extras(SMOKED_FISH, BEEF, MORE_SAUCE), drink()),
        "p46" to listOf(spice(), extras(FRIED_FISH, EGG, MORE_SAUCE, AVOCADO), drink()),

        // ---------- s5 : Snacks de Komi ----------
        "p8" to listOf(hot(), spice(), extras(BREAD, HOT_SAUCE_APART)),
        "p47" to listOf(hot(), sugar()),
        "p48" to listOf(hot(), toppings(SUGAR_TOP, HONEY)),
        "p49" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO), extras(CHEESE, EGG, SAUSAGE)),
        "p50" to listOf(spice(), remove(NO_ONION), extras(EGG, GARI)),
        "p51" to listOf(sauces(), spice(), remove(NO_ONION), extras(CHEESE, SAUSAGE_MORE)),
        "p52" to listOf(sauces(), spice(), remove(NO_ONION, NO_TOMATO, NO_SALAD), extras(CHEESE, EGG)),
        "p53" to listOf(popcornTaste()),
        "p54" to listOf(salt()),
        "p55" to listOf(hot(), toppings(SUGAR_TOP, CHOCOLATE, HONEY, BANANA)),
        "p56" to listOf(salt(), spice(), sauces()),

        // ---------- s6 : Le Coin du Poulet ----------
        "p57" to listOf(piece(), spice(), side(ATTIEKE, ALLOCO, FRIES, RICE), extras(ONIONS, HOT_SAUCE_APART), drink()),
        "p58" to listOf(grill(), piece(), spice(), side(ATTIEKE, ALLOCO, FRIES, RICE), remove(NO_ONION), drink()),
        "p59" to listOf(piece(), spice(), sauces(), side(FRIES, ALLOCO, RICE, ATTIEKE), drink()),
        "p60" to listOf(grill(), spice(), extras(ONIONS, BREAD, ALLOCO), drink()),
        "p61" to listOf(piece(), spice(), remove(NO_ONION, NO_TOMATO), extras(MORE_ATTIEKE, MORE_CHICKEN, ALLOCO), drink()),
        "p62" to listOf(piece(), spice(), remove(NO_ONION), extras(MORE_ALLOCO, MORE_CHICKEN), drink()),
        "p63" to listOf(piece(), spice(), extras(MORE_SAUCE, PLANTAIN, SALAD), drink()),
        "p64" to listOf(spice(), remove(NO_ONION), extras(EGG, CHEESE, MORE_CHICKEN), drink()),
        "p65" to listOf(
            single("chicken_type", "Poulet", o("fried_chicken", "Frit", "du poulet frit"), o("grilled_chicken", "Braisé", "du poulet braisé")),
            piece(), spice(), sauces(), drink()
        ),
        "p66" to listOf(strength(), extras(ONIONS)),
        "p67" to listOf(dressing(), remove(NO_TOMATO, NO_CUCUMBER, NO_EGG), extras(CHICKEN, BREAD)),
        "p68" to listOf(piece(), side(RICE, YAM, PASTE, ATTIEKE), spice(), extras(MORE_SAUCE), drink()),
        "p69" to listOf(spice(), remove(NO_ONION), extras(EGG, MORE_CHICKEN, SOY), drink()),
        "p70" to listOf(soda(), ice()),

        // ---------- s7 : Poissons Grillés d'Ama ----------
        "p71" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, YAM, FRIES), remove(NO_ONION), extras(LEMON, PEPPER_APART), drink()),
        "p72" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, YAM, FRIES), remove(NO_ONION), extras(LEMON, PEPPER_APART), drink()),
        "p73" to listOf(crispy(), spice(), side(ATTIEKE, ALLOCO, RICE, FRIES), extras(ONIONS, PEPPER_APART, LEMON), drink()),
        "p74" to listOf(spice(), side(RICE, YAM, ATTIEKE, PASTE), extras(MORE_SAUCE), remove(NO_ONION), drink()),
        "p75" to listOf(grill(), spice(), remove(NO_ONION, NO_TOMATO), extras(MORE_ATTIEKE, ALLOCO), drink()),
        "p76" to listOf(grill(), spice(), remove(NO_ONION), extras(MORE_ALLOCO, ATTIEKE), drink()),
        "p77" to listOf(ripeness(), spice(), extras(MORE_SAUCE, PEPPER_APART), drink()),
        "p78" to listOf(grill(), spice(), extras(MORE_SAUCE, PEPPER_APART), remove(NO_ONION), drink()),
        "p79" to listOf(spice(), extras(MORE_SAUCE, PLANTAIN, EGG), drink()),
        "p80" to listOf(spice(), extras(MORE_SMOKED, MORE_SAUCE), drink()),
        "p81" to listOf(strength(), extras(ONIONS)),
        "p82" to listOf(spice(), hot(), extras(MORE_GINGER, LEMON, BREAD)),
        "p83" to listOf(dressing(), remove(NO_TOMATO, NO_CUCUMBER, NO_ONION), extras(EGG)),
        "p84" to listOf(spice(), sauces(), extras(ONIONS, PEPPER_APART), drink()),

        // ---------- s8 : Chez Tantie Esi ----------
        "p85" to sauceDish(MORE_SAUCE, MORE_CHICKEN, SMOKED_FISH),
        "p86" to sauceDish(MORE_SAUCE, EGG, MORE_SMOKED),
        "p87" to sauceDish(MORE_SAUCE, MORE_CHICKEN, MORE_SMOKED),
        "p88" to sauceDish(MORE_SMOKED, BEEF, MORE_SAUCE),
        "p89" to sauceDish(SMOKED_FISH, BEEF, MORE_SAUCE),
        "p90" to sauceDish(MORE_SMOKED, BEEF, MORE_SAUCE),
        "p91" to sauceDish(MORE_SMOKED, BEEF, MORE_SAUCE),
        "p92" to sauceDish(MORE_SMOKED, BEEF, MORE_SAUCE),
        "p93" to listOf(sauceChoice(), spice(), extras(FISH, CHICKEN, BEEF, SMOKED_FISH)),
        "p94" to listOf(sauceChoice(), spice(), extras(FISH, CHICKEN, BEEF, SMOKED_FISH)),
        "p95" to listOf(sugar(), cold()),
        "p96" to listOf(spice(), extras(FRIED_FISH, EGG, SARDINES)),
        "p97" to listOf(garnish(), spice()),
        "p98" to listOf(sugar(), garnish()),
        "p99" to listOf(side(RICE, PASTE, YAM, ATTIEKE), spice(), extras(MORE_SAUCE, MORE_MEAT, EGG)),
        "p100" to listOf(side(RICE, GARI, BREAD), spice(), extras(EGG, FRIED_FISH, PLANTAIN, MORE_SAUCE)),

        // ---------- s9 : Snack Étoile ----------
        "p101" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO, NO_SALAD), extras(CHEESE, EGG, FRIES), drink()),
        "p102" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO, NO_SALAD), extras(CHEESE, EGG), drink()),
        "p103" to listOf(steak(), spice(), sauces(), remove(NO_ONION, NO_TOMATO, NO_SALAD), extras(CHEESE, EGG, FRIES), drink()),
        "p104" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO, NO_SALAD), extras(CHEESE, EGG, FRIES), drink()),
        "p105" to listOf(sauces(), spice(), remove(NO_ONION), extras(CHEESE, SAUSAGE_MORE), drink()),
        "p106" to listOf(spice(), sauces(), remove(NO_ONION, NO_TOMATO), extras(EGG, SAUSAGE)),
        "p107" to listOf(spice(), remove(NO_ONION), extras(EGG, GARI)),
        "p108" to listOf(hot(), sugar()),
        "p109" to listOf(cakeExtras()),
        "p110" to listOf(cakeExtras()),
        "p111" to listOf(hot(), toppings(SUGAR_TOP, LEMON, HONEY)),
        "p112" to listOf(spice(), remove(NO_ONION), extras(BREAD, CHEESE)),
        "p113" to listOf(
            single(
                "caramel", "Caramel",
                m("caramel_light", "Peu de caramel", "peu caramélisé"),
                o("caramel_normal", "Normal", null),
                m("caramel_more", "Bien caramélisé", "bien caramélisé")
            )
        ),
        "p114" to listOf(salt(), spice(), sauces()),
        "p115" to listOf(
            single(
                "flavor", "Parfum",
                o("mango_flavor", "Mangue", "un parfum mangue"),
                o("pineapple_flavor", "Ananas", "un parfum ananas"),
                o("banana_flavor", "Banane", "un parfum banane"),
                o("seasonal_flavor", "Fruits de saison", null)
            ),
            sugar(), cold()
        ),
        "p116" to listOf(pulp(), sugar(), ice()),

        // ---------- s10 : Pizza & Co Lomé ----------
        "p117" to listOf(pizzaCooking(), spice(), extras(PIZZA_CHEESE, OLIVES, EGG, MINCED), remove(NO_BASIL), drink()),
        "p118" to listOf(pizzaCooking(), spice(), extras(PIZZA_CHEESE, OLIVES, EGG, MORE_CHICKEN), remove(NO_ONION, NO_PEPPERS), drink()),
        "p119" to listOf(pizzaCooking(), spice(), extras(PIZZA_CHEESE, EGG, CHICKEN), remove(NO_ONION, NO_OLIVES), drink()),
        "p120" to listOf(spice(), tacosSauces(), remove(NO_FRIES), extras(CHEESE, EGG, MORE_CHICKEN), drink()),
        "p121" to listOf(spice(), tacosSauces(), remove(NO_FRIES), extras(CHEESE, EGG, MORE_MEAT), drink()),
        "p122" to listOf(spice(), remove(NO_ONION, NO_PEPPERS), extras(EGG, MORE_CHICKEN, SOY), drink()),
        "p123" to listOf(spice(), remove(NO_CARROT, NO_CABBAGE, NO_PEPPERS), extras(EGG, CHICKEN, SOY), drink()),
        "p124" to listOf(spice(), remove(NO_ONION), extras(GRATED_CHEESE, EGG, MORE_SAUCE), drink()),
        "p125" to listOf(
            single("gratin", "Gratinage", m("gratin", "Bien gratiné", "bien gratiné"), o("gratin_normal", "Normal", null)),
            spice(), extras(MORE_CHEESE, CHICKEN, EGG)
        ),
        "p126" to listOf(spice(), remove(NO_PEAS, NO_HAM), extras(EGG, CHICKEN, SOY), drink()),
        "p127" to listOf(dressing(), remove(NO_TOMATO, NO_CORN, NO_ONION), extras(EGG, CHEESE)),
        "p128" to listOf(spice(), hot(), remove(NO_ONION), extras(BREAD, CHEESE)),

        // ---------- s11 : Fruits Frais de Séna ----------
        "p129" to listOf(ripeness(fem = true), cut(fem = true)),
        "p130" to listOf(cut(), cold()),
        "p131" to listOf(ripeness(fem = true), cut(fem = true), extras(LEMON)),
        "p132" to listOf(cut(fem = true), cold()),
        "p133" to listOf(cut(fem = true, plural = true), cold()),
        "p134" to listOf(ripeness(fem = true, plural = true)),
        "p135" to listOf(ripeness(), cut(), extras(SALT_APART)),
        "p136" to listOf(fruitSaladExtras(), remove(NO_BANANA, NO_PAPAYA), cold()),
        "p137" to listOf(sugar(), ice(), extras(GINGER, MINT)),
        "p138" to listOf(texture(), sugar(), ice()),
        "p139" to listOf(pulp(), sugar(), ice()),
        "p140" to listOf(sugar(), ice(), extras(MINT)),
        "p141" to listOf(texture(), sugar(), ice(), extras(LEMON)),
        "p142" to listOf(sugar(), ice()),
        "p143" to listOf(strength(), sugar(), ice(), extras(LEMON, HONEY)),
        "p144" to listOf(sugar(), ice(), extras(MINT)),
        "p145" to listOf(sugar(), cold()),
        "p146" to listOf(cold()),

        // ---------- s12 : Maquis Le Palmier ----------
        "p147" to listOf(grill(), spice(), extras(ONIONS, TOMATOES, ALLOCO, ATTIEKE, BREAD), drink()),
        "p148" to listOf(grill(), spice(), brochetteSauce(), extras(BREAD, ALLOCO, TOMATOES), drink()),
        "p149" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, YAM, FRIES), remove(NO_ONION), extras(PEPPER_APART, LEMON), drink()),
        "p150" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, FRIES, RICE), extras(ONIONS, BREAD), drink()),
        "p151" to listOf(steak(), spice(), extras(ONIONS, ALLOCO, ATTIEKE, BREAD), drink()),
        "p152" to listOf(spice(), side(RICE, YAM, ATTIEKE, PASTE), extras(MORE_SAUCE), drink()),
        "p153" to listOf(spice(), side(RICE, YAM, ATTIEKE, PASTE), extras(MORE_SAUCE), drink()),
        "p154" to listOf(grill(), spice(), extras(ONIONS, LEMON, BREAD, ALLOCO), drink()),
        "p155" to listOf(grill(), spice(), extras(FRIED_FISH, EGG, TOMATO_SAUCE, PEPPER_APART)),
        "p156" to listOf(single("meat", "Viande", BEEF, MUTTON, GOAT, PORK), grill(), spice(), remove(NO_ONION), drink()),
        "p157" to listOf(grill(), spice(), remove(NO_ONION, NO_TOMATO), extras(MORE_ATTIEKE, ALLOCO), drink()),
        "p158" to listOf(spice(), extras(MORE_SAUCE, MORE_MEAT, ALLOCO, EGG), drink()),
        "p159" to listOf(spice(), hot(), extras(BREAD, LEMON, MORE_MEAT)),
        "p160" to listOf(spice(), remove(NO_ONION), extras(BREAD, SALT_APART)),
        "p161" to listOf(strength(), extras(ONIONS)),
        "p162" to listOf(soda(), ice()),

        // ---------- s13 : Délices de Dédé ----------
        "p163" to listOf(cakeExtras()),
        "p164" to listOf(
            single(
                "flavor", "Parfum",
                o("vanilla_flavor", "Vanille", "un parfum vanille"),
                o("chocolate_flavor", "Chocolat", "un parfum chocolat"),
                o("banana_cake_flavor", "Banane", "un parfum banane")
            ),
            extras(CANDLES, MESSAGE, GIFT, DECOR)
        ),
        "p165" to listOf(cakeExtras()),
        "p166" to listOf(hot(), toppings(CHOCOLATE, BANANA, SUGAR_TOP, HONEY)),
        "p167" to listOf(hot(), toppings(HONEY, CHOCOLATE, SUGAR_TOP)),
        "p168" to listOf(hot(), sugar()),
        "p169" to listOf(hot(), toppings(SUGAR_TOP, HONEY)),
        "p170" to listOf(hot(), spice(), extras(BREAD, HOT_SAUCE_APART)),
        "p171" to listOf(sugar(), cold(), extras(MORE_MIL, MORE_CURD)),
        "p172" to listOf(sugar(), cold()),
        "p173" to listOf(fruitSaladExtras(), cold()),
        "p174" to listOf(salt()),
        "p175" to listOf(salt()),
        "p176" to listOf(ripeness(), toppings(PEANUTS, HONEY, SUGAR_TOP, CHOCOLATE)),
        "p177" to listOf(sugar(), ice(), extras(MINT, VANILLA_ADD)),
        "p178" to listOf(strength(), sugar(), ice(), extras(HONEY)),

        // ---------- s14 : Chez Ablavi ----------
        "p179" to listOf(spice(), remove(NO_GARI, NO_EGG, NO_FISH), extras(MORE_EGG, MORE_FRIED_FISH, MORE_GARI, AVOCADO)),
        "p180" to listOf(sauceChoice(), spice(), extras(FRIED_FISH, CHICKEN, EGG), drink()),
        "p181" to listOf(side(CHICKEN, FISH, BEEF), spice(), extras(MORE_RICE, ALLOCO, SALAD), remove(NO_ONION), drink()),
        "p182" to listOf(oil(), spice(), extras(GARI, ALLOCO, EGG, FRIED_FISH), remove(NO_ONION)),
        "p183" to listOf(side(RICE, GARI, BREAD), spice(), extras(SMOKED_FISH, EGG, PLANTAIN)),
        "p184" to listOf(
            single("taste", "Goût", m("sweet_gari", "Sucré", "sucré"), m("salty_gari", "Salé", "salé"), o("plain_gari", "Nature", null)),
            extras(PEANUTS, MILK, ICE_CUBES)
        ),
        "p185" to listOf(spice(), remove(NO_ONION, NO_TOMATO), extras(EGG, SMOKED_FISH, FRIED_FISH, AVOCADO)),
        "p186" to listOf(sauceChoice(), spice(), extras(FRIED_FISH, SMOKED_FISH, EGG)),
        "p187" to listOf(sauceChoice(), spice(), extras(FRIED_FISH, SMOKED_FISH, EGG)),
        "p188" to listOf(spice(), extras(SMOKED_FISH, FRIED_FISH, CHICKEN, MORE_SAUCE), drink()),
        "p189" to listOf(spice(), extras(CHICKEN, SMOKED_FISH, MORE_SAUCE), drink()),
        "p190" to listOf(spice(), extras(FISH, EGG, MORE_SAUCE), drink()),
        "p191" to listOf(spice(), extras(CHICKEN, SMOKED_FISH, MORE_SAUCE), drink()),
        "p192" to listOf(spice(), extras(MORE_SAUCE, FRIED_FISH, EGG), drink()),

        // ---------- s15 : Fruits de Mer Baguida ----------
        "p193" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, RICE, FRIES), extras(LEMON, ONIONS, PEPPER_APART), drink()),
        "p194" to listOf(spice(), side(ATTIEKE, RICE, ALLOCO), extras(MORE_GARLIC, LEMON), remove(NO_ONION), drink()),
        "p195" to listOf(spice(), side(ATTIEKE, RICE, YAM, ALLOCO), extras(MORE_SAUCE, LEMON), drink()),
        "p196" to listOf(grill(), spice(), side(ATTIEKE, ALLOCO, RICE, FRIES), extras(LEMON, PEPPER_APART), drink()),
        "p197" to listOf(spice(), side(ATTIEKE, YAM, RICE, BREAD), extras(MORE_SAUCE), drink()),
        "p198" to listOf(spice(), hot(), extras(BREAD, LEMON, GINGER)),
        "p199" to listOf(crispy(), spice(), remove(NO_ONION, NO_TOMATO), extras(MORE_ATTIEKE, ALLOCO, PEPPER_APART), drink()),
        "p200" to listOf(spice(), remove(NO_ONION, NO_TOMATO), extras(MORE_SHRIMP, ALLOCO, FRIED_FISH), drink())
    )
}

/**
 * Construit le message envoyé au vendeur.
 *
 * Sans aucun choix, le résultat est identique à l'ancien message WhatsApp :
 * « Bonjour ! Je voudrais : Riz sauce arachide — Grande portion (1 500 FCFA). C'est disponible ? (via TogoFood) »
 *
 * Avec des choix :
 * « Bonjour ! Je voudrais : Poulet braisé — Demi (2 500 FCFA), bien cuit, avec une cuisse et de l'attiéké,
 *   sans piment. C'est disponible ? (via TogoFood) »
 *
 * Les adjectifs (`isModifier`) viennent d'abord, puis « avec … », puis les exclusions (`isWithout`).
 * Un supplément payant affiche son prix : « avec du fromage (+200 FCFA) ».
 *
 * Si [basePrice] est fourni et que des suppléments payants sont choisis, le message ajoute
 * « Total estimé : … FCFA ». Sans [basePrice], le message reste identique à l'ancien format.
 *
 * @param selections identifiants d'options choisies, par identifiant de groupe.
 * @param basePrice  prix de la variante choisie, en FCFA (optionnel).
 */
fun buildRequestMessage(
    dishName: String,
    variantLabel: String?,
    priceLabel: String?,
    groups: List<RequestOptionGroup>,
    selections: Map<String, Set<String>>,
    basePrice: Int? = null
): String {
    val chosen = chosenOptions(groups, selections)
    val modifiers = chosen.filter { it.isModifier && !it.isWithout }.mapNotNull { it.phrase }
    val with = chosen.filter { !it.isModifier && !it.isWithout }.mapNotNull { option ->
        option.phrase?.let { phrase ->
            if (option.isPaid) "$phrase (+${formatFcfa(option.extraPrice)})" else phrase
        }
    }
    val without = chosen.filter { it.isWithout }.mapNotNull { it.phrase }
    val extrasTotal = chosen.sumOf { it.extraPrice }

    return buildString {
        append("Bonjour ! Je voudrais : ").append(dishName)
        if (variantLabel != null) append(" — ").append(variantLabel)
        if (priceLabel != null) append(" (").append(priceLabel).append(")")
        if (modifiers.isNotEmpty()) append(", ").append(joinFrench(modifiers))
        if (with.isNotEmpty()) {
            append(if (modifiers.isNotEmpty()) ", avec " else " avec ").append(joinFrench(with))
        }
        if (without.isNotEmpty()) append(", ").append(joinFrench(without))
        if (basePrice != null && extrasTotal > 0) {
            append(". Total estimé : ").append(formatFcfa(basePrice + extrasTotal))
        }
        append(". C'est disponible ? (via TogoFood)")
    }
}

/** Options effectivement cochées, dans l'ordre des groupes. */
fun chosenOptions(
    groups: List<RequestOptionGroup>,
    selections: Map<String, Set<String>>
): List<RequestOption> = groups.flatMap { group ->
    val ids = selections[group.id].orEmpty()
    group.options.filter { it.id in ids }
}

/** Somme des suppléments payants choisis, en FCFA (0 si aucun). */
fun extrasTotal(
    groups: List<RequestOptionGroup>,
    selections: Map<String, Set<String>>
): Int = chosenOptions(groups, selections).sumOf { it.extraPrice }

/** 1500 → « 1 500 FCFA ». */
fun formatFcfa(amount: Int): String =
    amount.toString().reversed().chunked(3).joinToString(" ").reversed() + " FCFA"

/** « a », « a et b », « a, b et c ». */
private fun joinFrench(items: List<String>): String = when (items.size) {
    0 -> ""
    1 -> items.first()
    else -> items.dropLast(1).joinToString(", ") + " et " + items.last()
}