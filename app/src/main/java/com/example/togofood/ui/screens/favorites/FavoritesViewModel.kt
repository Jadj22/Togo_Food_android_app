package com.example.togofood.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.FavoritesRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class FavoritesTab { ASSIETTES, SPOTS }

/** Filtres contextuels street-food (pas des catégories génériques). */
enum class AssietteFilter { TOUT, CHAUD, A_DEUX_PAS }

data class FavoritesUiState(
    val preparations: List<Preparation> = emptyList(),
    val sellers: List<Seller> = emptyList(),
    val selectedTab: FavoritesTab = FavoritesTab.ASSIETTES,
    val assietteFilter: AssietteFilter = AssietteFilter.TOUT
) {
    val isCompletelyEmpty: Boolean get() = preparations.isEmpty() && sellers.isEmpty()

    val filteredPreparations: List<Preparation>
        get() = when (assietteFilter) {
            AssietteFilter.TOUT -> preparations
            AssietteFilter.CHAUD -> preparations.filter {
                it.availabilityStatus == AvailabilityStatus.AVAILABLE
            }
            AssietteFilter.A_DEUX_PAS -> preparations.filter { it.distanceMeters <= NEARBY_METERS }
        }

    val openSpots: Int
        get() = sellers.count { it.SellerOpenStatus == SellerOpenStatus.OPEN }

    companion object {
        const val NEARBY_METERS = 800
    }
}

/**
 * Expose assiettes + spots favoris, onglet actif et filtre « chaud / à deux pas ».
 * Repositories singletons V1 — swap Hilt/persistence plus tard sans toucher l'UI.
 */
class FavoritesViewModel : ViewModel() {

    private val selectedTab = MutableStateFlow(FavoritesTab.ASSIETTES)
    private val assietteFilter = MutableStateFlow(AssietteFilter.TOUT)

    private val catalogFavorites = combine(
        FavoritesRepository.ids,
        FavoritesRepository.sellerIds,
        CatalogRepository.preparations,
        CatalogRepository.sellers
    ) { _, _, _, _ ->
        FavoritesRepository.favorites() to FavoritesRepository.favoriteSellers()
    }

    val uiState: StateFlow<FavoritesUiState> = combine(
        catalogFavorites,
        selectedTab,
        assietteFilter
    ) { lists, tab, filter ->
        FavoritesUiState(
            preparations = lists.first,
            sellers = lists.second,
            selectedTab = tab,
            assietteFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoritesUiState(
            preparations = FavoritesRepository.favorites(),
            sellers = FavoritesRepository.favoriteSellers()
        )
    )

    fun selectTab(tab: FavoritesTab) {
        selectedTab.value = tab
    }

    fun setAssietteFilter(filter: AssietteFilter) {
        assietteFilter.update { filter }
    }

    fun toggle(preparationId: String) = FavoritesRepository.toggle(preparationId)

    fun toggleSeller(sellerId: String) = FavoritesRepository.toggleSeller(sellerId)
}
