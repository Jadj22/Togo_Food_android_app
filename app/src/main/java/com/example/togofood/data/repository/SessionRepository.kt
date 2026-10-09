package com.example.togofood.data.repository

import com.example.togofood.domain.model.UserRole
import com.example.togofood.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single source of truth for the logged-in user (guest by default).
 * V1 : en mémoire uniquement — même pattern que [FavoritesRepository].
 */
object SessionRepository {

    private val _session = MutableStateFlow(UserSession())
    val session: StateFlow<UserSession> = _session.asStateFlow()

    fun loginClient(displayName: String, phone: String) {
        _session.value = UserSession(
            isLoggedIn = true,
            role = UserRole.CLIENT,
            displayName = displayName.ifBlank { "Client" },
            phone = phone,
            sellerId = null,
        )
    }

    fun loginVendor(displayName: String, phone: String, sellerId: String) {
        _session.value = UserSession(
            isLoggedIn = true,
            role = UserRole.VENDEUR,
            displayName = displayName.ifBlank { "Vendeur" },
            phone = phone,
            sellerId = sellerId,
        )
    }

    fun logout() {
        _session.update { UserSession() }
    }
}
