package com.example.togofood.ui.screens.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.R
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.CategoryRepository
import com.example.togofood.data.repository.MockCategoryRepository
import com.example.togofood.data.repository.MockPreparationRepository
import com.example.togofood.data.repository.MockSellerRepository
import com.example.togofood.data.repository.PreparationRepository
import com.example.togofood.data.repository.SellerRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.SellerOpenStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Filtres rapides Home — une seule rangée (plus de modes redondants). */
enum class HomeFilter(@param:StringRes val labelRes: Int) {
    NEARBY(R.string.home_filter_nearby),
    POPULAR(R.string.home_filter_popular),
    AVAILABLE_NOW(R.string.home_filter_available),
    OPEN_NOW(R.string.home_filter_open),
    MIN_RATING(R.string.home_filter_rating),
    PRICE_RANGE(R.string.home_filter_price)
}

data class HomeFilterCounts(
    val nearby: Int = 0,
    val popular: Int = 0,
    val available: Int = 0,
    val open: Int = 0,
    val rating: Int = 0
) {
    fun forFilter(filter: HomeFilter): Int = when (filter) {
        HomeFilter.NEARBY -> nearby
        HomeFilter.POPULAR -> popular
        HomeFilter.AVAILABLE_NOW -> available
        HomeFilter.OPEN_NOW -> open
        HomeFilter.MIN_RATING -> rating
        HomeFilter.PRICE_RANGE -> -1 // sheet, pas de count chip
    }
}

class HomeViewModel(
    private val preparationRepo: PreparationRepository = MockPreparationRepository(),
    private val categoryRepo: CategoryRepository = MockCategoryRepository(),
    private val sellerRepo: SellerRepository = MockSellerRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _filters = MutableStateFlow<Set<HomeFilter>>(emptySet())
    val filters: StateFlow<Set<HomeFilter>> = _filters.asStateFlow()

    private val _nearby = MutableStateFlow<List<Preparation>>(emptyList())
    val nearby: StateFlow<List<Preparation>> = _nearby.asStateFlow()

    private val _availableNow = MutableStateFlow<List<Preparation>>(emptyList())
    val availableNow: StateFlow<List<Preparation>> = _availableNow.asStateFlow()

    private val _popular = MutableStateFlow<List<Preparation>>(emptyList())
    val popular: StateFlow<List<Preparation>> = _popular.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _results = MutableStateFlow<List<Preparation>>(emptyList())
    val results: StateFlow<List<Preparation>> = _results.asStateFlow()

    private val _isFiltering = MutableStateFlow(false)
    val isFiltering: StateFlow<Boolean> = _isFiltering.asStateFlow()

    private val _priceRange = MutableStateFlow<IntRange?>(null)
    val priceRange: StateFlow<IntRange?> = _priceRange.asStateFlow()

    private val _showPriceSheet = MutableStateFlow(false)
    val showPriceSheet: StateFlow<Boolean> = _showPriceSheet.asStateFlow()

    private val _filterCounts = MutableStateFlow(HomeFilterCounts())
    val filterCounts: StateFlow<HomeFilterCounts> = _filterCounts.asStateFlow()

    val openStatusBySeller: StateFlow<Map<String, SellerOpenStatus>> =
        CatalogRepository.sellers
            .map { sellers -> sellers.associate { it.uuid to it.SellerOpenStatus } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    init {
        loadInitialData()
        viewModelScope.launch {
            CatalogRepository.preparations.collect { refresh() }
        }
        viewModelScope.launch {
            CatalogRepository.sellers.collect { refresh() }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _categories.update { categoryRepo.getAll() }
            delay(1500)
            refresh()
            _isLoading.value = false
        }
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.update { category }
        refresh()
    }

    fun onFilterClick(filter: HomeFilter) {
        if (filter == HomeFilter.PRICE_RANGE) {
            _showPriceSheet.value = true
            return
        }
        _filters.update { if (filter in it) it - filter else it + filter }
        refresh()
    }

    fun dismissPriceSheet() {
        _showPriceSheet.value = false
    }

    fun setPriceRange(range: IntRange?) {
        _priceRange.update { range }
        _filters.update { current ->
            if (range != null) current + HomeFilter.PRICE_RANGE
            else current - HomeFilter.PRICE_RANGE
        }
        _showPriceSheet.value = false
        refresh()
    }

    /** Aperçu live pour CTA sheet prix. */
    fun previewResultCount(range: IntRange?): Int {
        val draftFilters = (_filters.value - HomeFilter.PRICE_RANGE).let { base ->
            if (range != null) base + HomeFilter.PRICE_RANGE else base
        }
        return applyFilters(
            base = baseList(_selectedCategory.value),
            filters = draftFilters,
            priceRange = range
        ).size
    }

    fun resetAll() {
        _selectedCategory.update { null }
        _filters.update { emptySet() }
        _priceRange.update { null }
        refresh()
    }

    private fun refresh() {
        val category = _selectedCategory.value
        val filters = _filters.value
        val priceRange = _priceRange.value

        if (category != null) {
            val filtered = preparationRepo.getByCategory(category.id)
            _nearby.update { filtered.sortedBy { it.distanceMeters } }
            _availableNow.update { filtered.filter { it.availabilityStatus == AvailabilityStatus.AVAILABLE } }
            _popular.update { filtered.filter { it.isPopular } }
        } else {
            _nearby.update { preparationRepo.getNearby() }
            _availableNow.update { preparationRepo.getAvailableNow() }
            _popular.update { preparationRepo.getPopular() }
        }

        val base = baseList(category)
        val list = applyFilters(base, filters, priceRange)
        _results.update { list }
        _isFiltering.update { category != null || filters.isNotEmpty() }
        _filterCounts.update { computeCounts(base) }
    }

    private fun baseList(category: Category?): List<Preparation> =
        if (category != null) preparationRepo.getByCategory(category.id)
        else preparationRepo.getAll()

    private fun applyFilters(
        base: List<Preparation>,
        filters: Set<HomeFilter>,
        priceRange: IntRange?
    ): List<Preparation> {
        var list = base
        if (HomeFilter.NEARBY in filters) list = list.filter { it.distanceMeters < 1000 }
        if (HomeFilter.POPULAR in filters) list = list.filter { it.isPopular }
        if (HomeFilter.AVAILABLE_NOW in filters) {
            list = list.filter { it.availabilityStatus == AvailabilityStatus.AVAILABLE }
        }
        if (HomeFilter.OPEN_NOW in filters) {
            list = list.filter {
                sellerRepo.getById(it.sellerId)?.SellerOpenStatus == SellerOpenStatus.OPEN
            }
        }
        if (HomeFilter.MIN_RATING in filters) list = list.filter { it.rating >= 4.0f }
        if (HomeFilter.PRICE_RANGE in filters && priceRange != null) {
            list = list.filter { it.basePrice in priceRange }
        }
        return list
    }

    private fun computeCounts(base: List<Preparation>): HomeFilterCounts {
        val openIds = CatalogRepository.sellers.value
            .filter { it.SellerOpenStatus == SellerOpenStatus.OPEN }
            .map { it.uuid }
            .toSet()
        return HomeFilterCounts(
            nearby = base.count { it.distanceMeters < 1000 },
            popular = base.count { it.isPopular },
            available = base.count { it.availabilityStatus == AvailabilityStatus.AVAILABLE },
            open = base.count { it.sellerId in openIds },
            rating = base.count { it.rating >= 4.0f }
        )
    }
}
