package com.example.togofood.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.MockCategoryRepository
import com.example.togofood.data.repository.MockPreparationRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer

/** Plafonds prix FCFA pour filtres discrets (chips, pas stepper). */
enum class SearchPriceCap(val maxFcfa: Int) {
    UP_TO_1000(1_000),
    UP_TO_2000(2_000),
    UP_TO_3000(3_000),
    UP_TO_5000(5_000);

    companion object {
        fun fromMax(max: Int?): SearchPriceCap? = entries.find { it.maxFcfa == max }
    }
}

data class SearchUiState(
    val query: String = "",
    val categories: List<Category> = emptyList(),
    val results: List<Preparation> = emptyList(),
    val sellerResults: List<Seller> = emptyList(),
    val filterOpenNow: Boolean = false,
    val filterPopular: Boolean = false,
    val filterNearby: Boolean = false,
    /** null = pas de plafond prix */
    val filterMaxPrice: Int? = null,
    /** null = pas de note min */
    val filterMinRating: Float? = null,
    /** Counts chips = marché avant sélection de ce chip */
    val countOpen: Int = 0,
    val countPopular: Int = 0,
    val countNearby: Int = 0,
    val isLoading: Boolean = false,
    val showFiltersSheet: Boolean = false
) {
    val hasQuickFilter: Boolean
        get() = filterOpenNow || filterPopular || filterNearby

    val hasAdvancedFilter: Boolean
        get() = filterMaxPrice != null || filterMinRating != null

    val hasActiveFilter: Boolean
        get() = hasQuickFilter || hasAdvancedFilter

    val isSearching: Boolean
        get() = query.isNotBlank() || hasActiveFilter

    val totalResultCount: Int
        get() = results.size + sellerResults.size
}

class SearchViewModel : ViewModel() {

    private val prepRepo = MockPreparationRepository()
    private val categoryRepo = MockCategoryRepository()

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        _state.update { it.copy(categories = categoryRepo.getAll()) }
        viewModelScope.launch {
            CatalogRepository.preparations.collect {
                if (_state.value.isSearching) executeSearch(showLoading = false)
                else refreshChipCounts()
            }
        }
        viewModelScope.launch {
            CatalogRepository.sellers.collect {
                if (_state.value.isSearching) executeSearch(showLoading = false)
                else refreshChipCounts()
            }
        }
        refreshChipCounts()
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        executeSearch()
    }

    fun toggleFilterOpenNow() {
        _state.update { it.copy(filterOpenNow = !it.filterOpenNow) }
        executeSearch()
    }

    fun toggleFilterPopular() {
        _state.update { it.copy(filterPopular = !it.filterPopular) }
        executeSearch()
    }

    fun toggleFilterNearby() {
        _state.update { it.copy(filterNearby = !it.filterNearby) }
        executeSearch()
    }

    fun openFiltersSheet() {
        _state.update { it.copy(showFiltersSheet = true) }
    }

    fun dismissFiltersSheet() {
        _state.update { it.copy(showFiltersSheet = false) }
    }

    fun applyAdvancedFilters(maxPrice: Int?, minRating: Float?) {
        _state.update {
            it.copy(
                filterMaxPrice = maxPrice,
                filterMinRating = minRating,
                showFiltersSheet = false
            )
        }
        executeSearch()
    }

    fun clearFilters() {
        _state.update {
            it.copy(
                filterOpenNow = false,
                filterPopular = false,
                filterNearby = false,
                filterMaxPrice = null,
                filterMinRating = null
            )
        }
        executeSearch()
    }

    fun searchCategory(categoryName: String) {
        _state.update { it.copy(query = categoryName) }
        executeSearch()
    }

    /** Aperçu live pour le CTA sheet — hors état appliqué. */
    fun previewResultCount(maxPrice: Int?, minRating: Float?): Int {
        val s = _state.value
        val (preps, sellers) = resolve(
            query = s.query,
            openNow = s.filterOpenNow,
            popular = s.filterPopular,
            nearby = s.filterNearby,
            maxPrice = maxPrice,
            minRating = minRating
        )
        return preps.size + sellers.size
    }

    private fun refreshChipCounts() {
        val (basePreps, baseSellers) = resolve(
            query = _state.value.query,
            openNow = false,
            popular = false,
            nearby = false,
            maxPrice = _state.value.filterMaxPrice,
            minRating = _state.value.filterMinRating
        )
        _state.update {
            it.copy(
                countOpen = countOpen(basePreps, baseSellers),
                countPopular = countPopular(basePreps, baseSellers),
                countNearby = countNearby(basePreps, baseSellers)
            )
        }
    }

    private fun executeSearch(showLoading: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val s = _state.value
            if (!s.isSearching) {
                val (basePreps, baseSellers) = resolve(
                    query = "",
                    openNow = false,
                    popular = false,
                    nearby = false,
                    maxPrice = null,
                    minRating = null
                )
                _state.update {
                    it.copy(
                        results = emptyList(),
                        sellerResults = emptyList(),
                        isLoading = false,
                        countOpen = countOpen(basePreps, baseSellers),
                        countPopular = countPopular(basePreps, baseSellers),
                        countNearby = countNearby(basePreps, baseSellers)
                    )
                }
                return@launch
            }

            if (showLoading) {
                _state.update { it.copy(isLoading = true) }
                delay(400)
            }

            val (preps, sellers) = resolve(
                query = s.query,
                openNow = s.filterOpenNow,
                popular = s.filterPopular,
                nearby = s.filterNearby,
                maxPrice = s.filterMaxPrice,
                minRating = s.filterMinRating
            )

            val (basePreps, baseSellers) = resolve(
                query = s.query,
                openNow = false,
                popular = false,
                nearby = false,
                maxPrice = s.filterMaxPrice,
                minRating = s.filterMinRating
            )

            _state.update {
                it.copy(
                    results = preps,
                    sellerResults = sellers,
                    isLoading = false,
                    countOpen = countOpen(basePreps, baseSellers),
                    countPopular = countPopular(basePreps, baseSellers),
                    countNearby = countNearby(basePreps, baseSellers)
                )
            }
        }
    }

    private fun resolve(
        query: String,
        openNow: Boolean,
        popular: Boolean,
        nearby: Boolean,
        maxPrice: Int?,
        minRating: Float?
    ): Pair<List<Preparation>, List<Seller>> {
        val prepMatches = if (query.isBlank()) {
            CatalogRepository.preparations.value
        } else {
            prepRepo.search(query)
        }

        var preps = prepMatches
        if (openNow) preps = preps.filter { it.availabilityStatus == AvailabilityStatus.AVAILABLE }
        if (popular) preps = preps.filter { it.isPopular }
        if (nearby) preps = preps.filter { it.distanceMeters < 1000 }
        if (maxPrice != null) preps = preps.filter { it.basePrice <= maxPrice }
        if (minRating != null) preps = preps.filter { it.rating >= minRating }

        val q = query.trim().fold()
        val sellerIdsFromPreps = prepMatches.map { it.sellerId }.toSet()
        var sellers = CatalogRepository.sellers.value.filter { seller ->
            q.isBlank() || seller.matches(q) || seller.uuid in sellerIdsFromPreps
        }
        if (openNow) sellers = sellers.filter { it.SellerOpenStatus == SellerOpenStatus.OPEN }
        if (popular) sellers = sellers.filter { it.rating >= 4.5f }
        if (nearby) sellers = sellers.filter { it.distanceMeters < 1000 }
        if (minRating != null) sellers = sellers.filter { it.rating >= minRating }
        // Prix : un vendeur match si ≥1 prep (du catalogue) sous le plafond
        if (maxPrice != null) {
            val sellerIdsCheap = CatalogRepository.preparations.value
                .filter { it.basePrice <= maxPrice }
                .map { it.sellerId }
                .toSet()
            sellers = sellers.filter { it.uuid in sellerIdsCheap }
        }
        sellers = sellers.sortedBy { it.distanceMeters }

        return preps to sellers
    }

    private fun countOpen(preps: List<Preparation>, sellers: List<Seller>): Int =
        preps.count { it.availabilityStatus == AvailabilityStatus.AVAILABLE } +
            sellers.count { it.SellerOpenStatus == SellerOpenStatus.OPEN }

    private fun countPopular(preps: List<Preparation>, sellers: List<Seller>): Int =
        preps.count { it.isPopular } + sellers.count { it.rating >= 4.5f }

    private fun countNearby(preps: List<Preparation>, sellers: List<Seller>): Int =
        preps.count { it.distanceMeters < 1000 } + sellers.count { it.distanceMeters < 1000 }

    private fun Seller.matches(foldedQuery: String): Boolean =
        listOf(name, description, zone, landmark).any { it.fold().contains(foldedQuery) }

    private fun String.fold(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
}
