package com.example.togofood.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Centralized mapping of food category IDs to emojis and pastel background colors.
 * Single source of truth — avoids duplication across screens.
 */




object FoodIcons {

    data class FoodCategory(
        val id: String,
        val name: String,
        val emoji: String,
        val popularity: Int
    )

    // popularity: 5 = très populaire, 1 = plus spécifique
    val categories = listOf(
        // 1–20 : Plats et préparations populaires
        FoodCategory("ayimolou", "Ayimolou", "🍛", 5),
        FoodCategory("riz", "Riz", "🍚", 5),
        FoodCategory("riz_sauce", "Riz sauce", "🍛", 5),
        FoodCategory("riz_gras", "Riz gras", "🍚", 5),
        FoodCategory("riz_tomate", "Riz à la tomate", "🍅", 4),
        FoodCategory("riz_cantonais", "Riz cantonais", "🍳", 3),
        FoodCategory("poulet", "Poulet", "🍗", 5),
        FoodCategory("poulet_braise", "Poulet braisé", "🍗", 5),
        FoodCategory("poulet_frit", "Poulet frit", "🍗", 4),
        FoodCategory("pate", "Pâte de maïs", "🥣", 5),
        FoodCategory("fufu", "Foufou", "🥣", 5),
        FoodCategory("akoume", "Akoumé", "🥣", 5),
        FoodCategory("akple", "Akplè", "🥣", 4),
        FoodCategory("abolo", "Ablo", "🫓", 4),
        FoodCategory("kom", "Kom", "🫔", 3),
        FoodCategory("akpan", "Akpan", "🥛", 3),
        FoodCategory("attieke", "Attiéké", "🍚", 4),
        FoodCategory("spaghetti", "Spaghetti", "🍝", 4),
        FoodCategory("macaroni", "Macaroni", "🍝", 3),
        FoodCategory("igname", "Igname", "🍠", 4),

        // 21–40 : Plats traditionnels et accompagnements
        FoodCategory("haricots", "Haricots", "🫘", 4),
        FoodCategory("haricots_rouges", "Haricots rouges", "🫘", 3),
        FoodCategory("gari", "Gari", "🥣", 4),
        FoodCategory("gari_foto", "Gari foto", "🍳", 4),
        FoodCategory("akassa", "Akassa", "🥣", 3),
        FoodCategory("ablo_mais", "Ablo de maïs", "🫓", 3),
        FoodCategory("agbelima", "Agbélima", "🥣", 3),
        FoodCategory("pinon", "Pinon", "🍲", 3),
        FoodCategory("koklo_mem", "Koklo mém", "🍗", 3),
        FoodCategory("gboma", "Gboma", "🥬", 4),
        FoodCategory("ademe", "Adémè", "🥬", 4),
        FoodCategory("feuille_manioc", "Feuilles de manioc", "🌿", 3),
        FoodCategory("sauce_arachide", "Sauce arachide", "🥜", 4),
        FoodCategory("sauce_tomate", "Sauce tomate", "🍅", 5),
        FoodCategory("sauce_graine", "Sauce graine", "🍲", 3),
        FoodCategory("sauce_gombo", "Sauce gombo", "🥣", 4),
        FoodCategory("sauce_epinards", "Sauce aux feuilles", "🥬", 3),
        FoodCategory("akoume_ademe", "Akoumé avec adémè", "🥣", 4),
        FoodCategory("akoume_gboma", "Akoumé avec gboma", "🥬", 3),
        FoodCategory("frites_igname", "Frites d'igname", "🍟", 4),

        // 41–55 : Viandes, poissons et grillades
        FoodCategory("poisson", "Poisson", "🐟", 5),
        FoodCategory("poisson_braise", "Poisson braisé", "🐟", 5),
        FoodCategory("poisson_frit", "Poisson frit", "🐟", 4),
        FoodCategory("tilapia", "Tilapia", "🐟", 4),
        FoodCategory("viande_boeuf", "Bœuf", "🥩", 4),
        FoodCategory("viande_mouton", "Mouton", "🍖", 3),
        FoodCategory("viande_chevre", "Chèvre", "🍖", 3),
        FoodCategory("porc", "Porc", "🥩", 3),
        FoodCategory("brochettes", "Brochettes", "🍢", 5),
        FoodCategory("brochettes_boeuf", "Brochettes de bœuf", "🍢", 4),
        FoodCategory("brochettes_poulet", "Brochettes de poulet", "🍢", 4),
        FoodCategory("soya", "Soya", "🥩", 4),
        FoodCategory("akpan_viande", "Viande en sauce", "🍲", 3),
        FoodCategory("omelette", "Omelette", "🍳", 4),
        FoodCategory("oeufs_bouillis", "Œufs bouillis", "🥚", 4),

        // 56–70 : Street food et snacks
        FoodCategory("piment", "Piment", "🌶️", 4),
        FoodCategory("alloco", "Alloco", "🍌", 5),
        FoodCategory("banane_bouillie", "Banane plantain bouillie", "🍌", 3),
        FoodCategory("banane_grillee", "Banane plantain grillée", "🍌", 4),
        FoodCategory("frites_pomme", "Frites de pommes de terre", "🍟", 4),
        FoodCategory("sandwich", "Sandwich", "🥪", 4),
        FoodCategory("hamburger", "Hamburger", "🍔", 3),
        FoodCategory("hot_dog", "Hot-dog", "🌭", 2),
        FoodCategory("pain_omelette", "Pain omelette", "🥪", 5),
        FoodCategory("pain_haricots", "Pain haricots", "🥖", 4),
        FoodCategory("beignets", "Beignets", "🍩", 5),
        FoodCategory("beignets_banane", "Beignets de banane", "🍌", 3),
        FoodCategory("akara", "Akara", "🧆", 3),
        FoodCategory("popcorn", "Pop-corn", "🍿", 2),
        FoodCategory("arachides", "Arachides grillées", "🥜", 4),

        // 71–82 : Boissons locales et jus
        FoodCategory("jus_bissap", "Jus de bissap", "🧃", 5),
        FoodCategory("jus_gingembre", "Jus de gingembre", "🥤", 5),
        FoodCategory("jus_ananas", "Jus d'ananas", "🍍", 4),
        FoodCategory("jus_tamarin", "Jus de tamarin", "🥤", 3),
        FoodCategory("jus_citron", "Jus de citron", "🍋", 4),
        FoodCategory("jus_mangue", "Jus de mangue", "🥭", 4),
        FoodCategory("jus_papaye", "Jus de papaye", "🥤", 3),
        FoodCategory("jus_orange", "Jus d'orange", "🍊", 4),
        FoodCategory("degue", "Dégué", "🥛", 4),
        FoodCategory("yaourt", "Yaourt", "🥛", 4),
        FoodCategory("eau", "Eau", "💧", 5),
        FoodCategory("soda", "Boissons gazeuses", "🥤", 4),

        // 83–92 : Fruits et desserts
        FoodCategory("mangue", "Mangue", "🥭", 4),
        FoodCategory("ananas", "Ananas", "🍍", 4),
        FoodCategory("papaye", "Papaye", "🍈", 3),
        FoodCategory("pastèque", "Pastèque", "🍉", 4),
        FoodCategory("orange", "Orange", "🍊", 4),
        FoodCategory("banane", "Banane", "🍌", 5),
        FoodCategory("avocat", "Avocat", "🥑", 3),
        FoodCategory("salade_fruits", "Salade de fruits", "🍇", 3),
        FoodCategory("crepe", "Crêpes", "🥞", 3),
        FoodCategory("gateau", "Gâteau", "🎂", 4),

        // 93–100 : Plats complémentaires et cuisine internationale
        FoodCategory("pizza", "Pizza", "🍕", 3),
        FoodCategory("tacos", "Tacos", "🌮", 2),
        FoodCategory("nouilles", "Nouilles", "🍜", 3),
        FoodCategory("salade", "Salade", "🥗", 3),
        FoodCategory("soupe", "Soupe", "🍲", 4),
        FoodCategory("crevettes", "Crevettes", "🍤", 2),
        FoodCategory("crabe", "Crabe", "🦀", 2),
        FoodCategory("escargots", "Escargots", "🐌", 1)
    )

    fun emojiFor(categoryId: String): String =
        categories.find { it.id == categoryId.lowercase() }?.emoji ?: "🍽️"

    fun nameFor(categoryId: String): String =
        categories.find { it.id == categoryId.lowercase() }?.name
            ?: categoryId.replaceFirstChar { it.uppercase() }

    fun popularityFor(categoryId: String): Int =
        categories.find { it.id == categoryId.lowercase() }?.popularity ?: 1

    val pastelBackgrounds: Map<String, Color> = mapOf(
        "ayimolou" to Color(0xFFFFE8D6),
        "riz" to Color(0xFFFFF3E0),
        "poulet" to Color(0xFFFFE9D6),
        "pate" to Color(0xFFE8F5E9),
        "fufu" to Color(0xFFF3E5F5),
        "attieke" to Color(0xFFFFF8E1),
        "poisson" to Color(0xFFE1F5FE),
        "brochettes" to Color(0xFFFFEBEE),
        "alloco" to Color(0xFFFFF8E1),
        "jus_bissap" to Color(0xFFFCE4EC),
        "jus_gingembre" to Color(0xFFFFF3E0),
        "degue" to Color(0xFFE0F7FA),
        "beignets" to Color(0xFFF3E5F5),
        "soya" to Color(0xFFFFE9D6),
        "pizza" to Color(0xFFFFF3E0)
    )

    val defaultPastel: Color = Color(0xFFF5F5F5)

    fun backgroundFor(categoryId: String): Color =
        pastelBackgrounds[categoryId.lowercase()] ?: defaultPastel
}
