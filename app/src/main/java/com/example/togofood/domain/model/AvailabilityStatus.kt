package com.example.togofood.domain.model

import androidx.compose.ui.graphics.Color

enum class AvailabilityStatus(
    val label: String,
    val emoji: String,
    val color: Color
) {
    AVAILABLE("Disponible", "🟢", Color(0xFF4CAF50)),
    UNAVAILABLE("Indisponible", "🔴", Color(0xFFF44336)),
    TO_CHECK("À vérifier", "🟡", Color(0xFFFF9800)),
    CLOSES_SOON("Ferme bientôt", "🟠", Color(0xFFFF5722)),
    UNKNOWN("Inconnu", "⚪", Color(0xFF9E9E9E))
}
