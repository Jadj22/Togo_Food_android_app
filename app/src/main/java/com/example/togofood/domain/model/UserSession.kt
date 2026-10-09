package com.example.togofood.domain.model

enum class UserRole { CLIENT, VENDEUR }

/**
 * Session utilisateur légère (V1 en mémoire).
 * Remplaçable plus tard par DataStore / Firebase sans changer l’UI.
 */
data class UserSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole? = null,
    val displayName: String = "",
    val phone: String = "",
    val sellerId: String? = null,
) {
    val isVendor: Boolean get() = isLoggedIn && role == UserRole.VENDEUR && !sellerId.isNullOrBlank()
}
