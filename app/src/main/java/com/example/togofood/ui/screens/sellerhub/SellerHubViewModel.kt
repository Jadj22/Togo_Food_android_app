package com.example.togofood.ui.screens.sellerhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.SellerScheduleResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class HubPrepFilter {
    ALL,
    AVAILABLE,
    UNAVAILABLE
}

/** Grille = ID visuelle ; liste = toggles rapides (pas de défaut universel). */
enum class HubCatalogLayout {
    GRID,
    LIST
}

data class SellerHubUiState(
    val seller: Seller? = null,
    val preparations: List<Preparation> = emptyList(),
    val filteredPreparations: List<Preparation> = emptyList(),
    val prepFilter: HubPrepFilter = HubPrepFilter.ALL,
    val catalogLayout: HubCatalogLayout = HubCatalogLayout.LIST,
    val isOpen: Boolean = false,
    val availableCount: Int = 0,
    val unavailableCount: Int = 0,
    val scheduleSummary: String = "",
    /** Catégorie dominante pour hero (appétit / identité métier). */
    val heroCategoryId: String? = null,
    /** Ouvert mais aucun plat en vente — décision cassée côté client. */
    val showEmptyStockWarning: Boolean = false,
    val missingSession: Boolean = false,
)

class SellerHubViewModel : ViewModel() {

    private val sellerId: String?
        get() = SessionRepository.session.value.sellerId

    private val _prepFilter = MutableStateFlow(HubPrepFilter.ALL)
    private val _catalogLayout = MutableStateFlow(HubCatalogLayout.LIST)

    val uiState: StateFlow<SellerHubUiState> = combine(
        SessionRepository.session,
        CatalogRepository.sellers,
        CatalogRepository.preparations,
        _prepFilter,
        _catalogLayout,
    ) { session, sellers, preparations, prepFilter, catalogLayout ->
        val id = session.sellerId
        if (!session.isVendor || id.isNullOrBlank()) {
            SellerHubUiState(missingSession = true)
        } else {
            val seller = sellers.find { it.uuid == id }
            val preps = preparations.filter { it.sellerId == id }
            val available = preps.count { it.availabilityStatus == AvailabilityStatus.AVAILABLE }
            val unavailable = preps.size - available
            val filtered = when (prepFilter) {
                HubPrepFilter.ALL -> preps
                HubPrepFilter.AVAILABLE -> preps.filter {
                    it.availabilityStatus == AvailabilityStatus.AVAILABLE
                }
                HubPrepFilter.UNAVAILABLE -> preps.filter {
                    it.availabilityStatus != AvailabilityStatus.AVAILABLE
                }
            }
            val open = seller?.let {
                SellerScheduleResolver.effectiveOpenStatus(it) == SellerOpenStatus.OPEN
            } == true
            val summary = seller?.let {
                SellerScheduleResolver.summaryLines(it.schedule).firstOrNull().orEmpty()
            }.orEmpty()
            SellerHubUiState(
                seller = seller,
                preparations = preps,
                filteredPreparations = filtered,
                prepFilter = prepFilter,
                catalogLayout = catalogLayout,
                isOpen = open,
                availableCount = available,
                unavailableCount = unavailable,
                scheduleSummary = summary,
                heroCategoryId = dominantCategory(preps),
                showEmptyStockWarning = open && preps.isNotEmpty() && available == 0,
                missingSession = seller == null,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SellerHubUiState(),
    )

    fun setPrepFilter(filter: HubPrepFilter) {
        _prepFilter.value = filter
    }

    fun setCatalogLayout(layout: HubCatalogLayout) {
        _catalogLayout.value = layout
    }

    fun setShopOpen(open: Boolean) {
        val id = sellerId ?: return
        CatalogRepository.setShopOpen(id, open)
    }

    fun setPreparationAvailable(preparationId: String, available: Boolean) {
        CatalogRepository.setPreparationAvailability(preparationId, available)
    }

    fun isPreparationAvailable(prep: Preparation): Boolean =
        prep.availabilityStatus == AvailabilityStatus.AVAILABLE

    private fun dominantCategory(preps: List<Preparation>): String? =
        preps.groupingBy { it.categoryId }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
}
