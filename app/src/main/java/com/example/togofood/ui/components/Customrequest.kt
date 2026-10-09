package com.example.togofood.ui.components

import com.example.togofood.domain.model.Preparation


/*
 * Demande personnalisée (PRD — « Demande personnalisée »)
 *
 * L'utilisateur précise ce qu'il veut (accompagnement, piment, suppléments, boisson…)
 * puis envoie un message au vendeur. Ce n'est PAS une commande :
 *   - aucun panier, aucun stock, aucun paiement ;
 *   - les options n'ont pas de prix ;
 *   - le vendeur reste libre de répondre « c'est fini », « j'ai du poulet à la place », etc.
 */

/** Une seule option possible dans le groupe, ou plusieurs. */
enum class OptionSelection { SINGLE, MULTIPLE }

/**
 * Une option proposée à l'utilisateur (« Poisson », « Un peu de piment »…).
 *
 * @param phrase    fragment inséré dans la phrase du message (« du poisson »).
 *                  `null` = ne rien écrire (ex. « Aucun », « Aucune »).
 * @param isWithout vrai pour une exclusion (« sans piment ») : placée en fin de phrase
 *                  plutôt que derrière « avec ».
 */
data class RequestOption(
    val id: String,
    val label: String,
    val phrase: String?,
    val emoji: String? = null,
    val isWithout: Boolean = false
)

data class RequestOptionGroup(
    val id: String,
    val title: String,
    val selection: OptionSelection,
    val options: List<RequestOption>
)

/**
 * Source des options proposées pour une préparation, selon sa catégorie.
 *
 * `emptyList()` → la section « Personnaliser » est masquée sur la page détail.
 * Quand l'API exposera de vraies options par plat/vendeur, seul cet objet change.
 */
object RequestOptions {

    fun groupsFor(prep: Preparation): List<RequestOptionGroup> = when (prep.categoryId) {
        "riz", "pate", "fufu", "poulet" -> sauceMealGroups
        "brochettes" -> brochetteGroups
        "snacks" -> snackGroups
        "jus", "boissons" -> drinkGroups
        else -> emptyList()
    }

    /** Plats type sauce + riz / pâte / fufu. */
    private val sauceMealGroups = listOf(
        RequestOptionGroup(
            id = "side",
            title = "Accompagnement",
            selection = OptionSelection.SINGLE,
            options = listOf(
                RequestOption("chicken", "Poulet", phrase = "du poulet", emoji = "🍗"),
                RequestOption("fish", "Poisson", phrase = "du poisson", emoji = "🐟"),
                RequestOption("egg", "Œuf", phrase = "un œuf", emoji = "🥚"),
                RequestOption("no_side", "Aucun", phrase = null)
            )
        ),
        spiceGroup(),
        RequestOptionGroup(
            id = "extras",
            title = "Suppléments",
            selection = OptionSelection.MULTIPLE,
            options = listOf(
                RequestOption("macaron", "Macaron", phrase = "un macaron"),
                RequestOption("more_sauce", "Plus de sauce", phrase = "plus de sauce"),
                RequestOption("more_rice", "Plus de riz", phrase = "plus de riz")
            )
        ),
        drinkSideGroup()
    )

    private val brochetteGroups = listOf(
        spiceGroup(),
        RequestOptionGroup(
            id = "sauce",
            title = "Sauce",
            selection = OptionSelection.SINGLE,
            options = listOf(
                RequestOption("peanut", "Sauce arachide", phrase = "de la sauce arachide"),
                RequestOption("chili", "Sauce piment", phrase = "de la sauce piment"),
                RequestOption("onion", "Oignon", phrase = "des oignons"),
                RequestOption("no_sauce", "Aucune", phrase = null)
            )
        ),
        RequestOptionGroup(
            id = "extras",
            title = "Suppléments",
            selection = OptionSelection.MULTIPLE,
            options = listOf(
                RequestOption("extra_meat", "Plus de viande", phrase = "plus de viande"),
                RequestOption("bread", "Pain", phrase = "du pain")
            )
        ),
        drinkSideGroup()
    )

    private val snackGroups = listOf(
        RequestOptionGroup(
            id = "temp",
            title = "Température",
            selection = OptionSelection.SINGLE,
            options = listOf(
                RequestOption("hot", "Bien chaud", phrase = "bien chaud"),
                RequestOption("warm", "Tiède", phrase = "tiède"),
                RequestOption("as_is", "Comme d'habitude", phrase = null)
            )
        ),
        spiceGroup(),
        RequestOptionGroup(
            id = "extras",
            title = "Suppléments",
            selection = OptionSelection.MULTIPLE,
            options = listOf(
                RequestOption("more", "Plus de pièces", phrase = "plus de pièces"),
                RequestOption("sauce", "Sauce", phrase = "de la sauce")
            )
        ),
        drinkSideGroup()
    )

    private val drinkGroups = listOf(
        RequestOptionGroup(
            id = "sugar",
            title = "Sucre",
            selection = OptionSelection.SINGLE,
            options = listOf(
                RequestOption("no_sugar", "Sans sucre", phrase = "sans sucre", isWithout = true),
                RequestOption("light", "Peu sucré", phrase = "peu sucré"),
                RequestOption("normal", "Normal", phrase = null),
                RequestOption("sweet", "Bien sucré", phrase = "bien sucré")
            )
        ),
        RequestOptionGroup(
            id = "ice",
            title = "Glace",
            selection = OptionSelection.SINGLE,
            options = listOf(
                RequestOption("no_ice", "Sans glaçons", phrase = "sans glaçons", isWithout = true),
                RequestOption("ice", "Avec glaçons", phrase = "des glaçons"),
                RequestOption("as_is", "Comme d'habitude", phrase = null)
            )
        ),
        RequestOptionGroup(
            id = "extras",
            title = "Extras",
            selection = OptionSelection.MULTIPLE,
            options = listOf(
                RequestOption("lemon", "Citron", phrase = "du citron"),
                RequestOption("ginger", "Gingembre", phrase = "du gingembre")
            )
        )
    )

    private fun spiceGroup() = RequestOptionGroup(
        id = "spice",
        title = "Piment",
        selection = OptionSelection.SINGLE,
        options = listOf(
            RequestOption("no_spice", "Sans piment", phrase = "sans piment", isWithout = true),
            RequestOption("mild_spice", "Un peu", phrase = "un peu de piment", emoji = "🌶️"),
            RequestOption("normal_spice", "Normal", phrase = "du piment", emoji = "🌶️🌶️"),
            RequestOption("hot_spice", "Beaucoup", phrase = "beaucoup de piment", emoji = "🌶️🌶️🌶️")
        )
    )

    private fun drinkSideGroup() = RequestOptionGroup(
        id = "drink",
        title = "Boisson",
        selection = OptionSelection.SINGLE,
        options = listOf(
            RequestOption("bissap", "Jus de bissap", phrase = "un jus de bissap"),
            RequestOption("ginger", "Jus de gingembre", phrase = "un jus de gingembre"),
            RequestOption("water", "Eau", phrase = "de l'eau"),
            RequestOption("no_drink", "Aucune", phrase = null)
        )
    )
}

/**
 * Construit le message envoyé au vendeur.
 *
 * Sans aucun choix, le résultat est identique à l'ancien message WhatsApp :
 * « Bonjour ! Je voudrais : Riz sauce arachide — Grande portion (1 500 FCFA). C'est disponible ? (via TogoFood) »
 *
 * Avec des choix :
 * « Bonjour ! Je voudrais : Riz sauce arachide — Grande portion (1 500 FCFA) avec du poisson,
 *   un peu de piment, un macaron et un jus de bissap. C'est disponible ? (via TogoFood) »
 *
 * @param selections identifiants d'options choisies, par identifiant de groupe.
 */
fun buildRequestMessage(
    dishName: String,
    variantLabel: String?,
    priceLabel: String?,
    groups: List<RequestOptionGroup>,
    selections: Map<String, Set<String>>
): String {
    val chosen = groups.flatMap { group ->
        val ids = selections[group.id].orEmpty()
        group.options.filter { it.id in ids }
    }
    val with = chosen.filter { !it.isWithout }.mapNotNull { it.phrase }
    val without = chosen.filter { it.isWithout }.mapNotNull { it.phrase }

    return buildString {
        append("Bonjour ! Je voudrais : ").append(dishName)
        if (variantLabel != null) append(" — ").append(variantLabel)
        if (priceLabel != null) append(" (").append(priceLabel).append(")")
        if (with.isNotEmpty()) append(" avec ").append(joinFrench(with))
        if (without.isNotEmpty()) append(", ").append(joinFrench(without))
        append(". C'est disponible ? (via TogoFood)")
    }
}

/** « a », « a et b », « a, b et c ». */
private fun joinFrench(items: List<String>): String = when (items.size) {
    0 -> ""
    1 -> items.first()
    else -> items.dropLast(1).joinToString(", ") + " et " + items.last()
}
