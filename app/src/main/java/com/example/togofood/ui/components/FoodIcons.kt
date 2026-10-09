package com.example.togofood.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Centralized mapping of food category IDs to emojis and pastel background colors.
 * Single source of truth — avoids duplication across screens.
 */
object FoodIcons {

    fun emojiFor(categoryId: String): String = when (categoryId) {
        "riz"        -> "🍚"
        "jus"        -> "🥤"
        "poulet"     -> "🍗"
        "pate"       -> "🍲"
        "fufu"       -> "🫕"
        "snacks"     -> "🥨"
        "brochettes" -> "🍢"
        "boissons"   -> "🧃"
        else         -> "🍽️"
    }

    val pastelBackgrounds: Map<String, Color> = mapOf(
        "riz"        to Color(0xFFFFF3E0),
        "jus"        to Color(0xFFE1F5FE),
        "poulet"     to Color(0xFFFFF8E1),
        "pate"       to Color(0xFFE8F5E9),
        "fufu"       to Color(0xFFF3E5F5),
        "snacks"     to Color(0xFFFFFDE7),
        "brochettes" to Color(0xFFFFEBEE),
        "boissons"   to Color(0xFFE0F7FA),
    )

    val defaultPastel: Color = Color(0xFFF5F5F5)
}
