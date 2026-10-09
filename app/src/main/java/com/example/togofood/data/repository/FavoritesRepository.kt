package com.example.togofood.data.repository

import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single source of truth for the user's favorite preparations and sellers.
 *
 * V1 : en mémoire uniquement (pas de persistance disque). Le design permet de
 * remplacer l'implémentation par DataStore/Firebase plus tard sans toucher au UI :
 * la surface publique (ids / toggle / list) reste identique.
 */
object FavoritesRepository {

    private val _ids = MutableStateFlow<Set<String>>(emptySet())
    private val _sellerIds = MutableStateFlow<Set<String>>(emptySet())

    /** IDs des préparations favorites — observé par l'UI via collectAsState(). */
    val ids: StateFlow<Set<String>> = _ids.asStateFlow()

    /** IDs des vendeurs favoris. */
    val sellerIds: StateFlow<Set<String>> = _sellerIds.asStateFlow()

    fun isFavorite(preparationId: String): Boolean = preparationId in _ids.value

    fun isSellerFavorite(sellerId: String): Boolean = sellerId in _sellerIds.value

    fun toggle(preparationId: String) {
        _ids.update { current ->
            if (preparationId in current) current - preparationId else current + preparationId
        }
    }

    fun toggleSeller(sellerId: String) {
        _sellerIds.update { current ->
            if (sellerId in current) current - sellerId else current + sellerId
        }
    }

    /** Snapshot des assiettes favorites, résolu via le catalogue partagé. */
    fun favorites(): List<Preparation> =
        _ids.value.mapNotNull { CatalogRepository.getPreparation(it) }

    /** Snapshot des spots (boutiques) favoris. */
    fun favoriteSellers(): List<Seller> =
        _sellerIds.value.mapNotNull { CatalogRepository.getSeller(it) }
}
