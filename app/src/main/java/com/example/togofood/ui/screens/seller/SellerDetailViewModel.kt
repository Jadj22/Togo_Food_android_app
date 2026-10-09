package com.example.togofood.ui.screens.seller

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SellerDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sellerId: String = checkNotNull(savedStateHandle["sellerId"])

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val seller: StateFlow<Seller?> = CatalogRepository.sellers
        .combine(_isLoading) { sellers, loading ->
            if (loading) null else sellers.find { it.uuid == sellerId }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val menu: StateFlow<List<Preparation>> = CatalogRepository.preparations
        .combine(_isLoading) { preparations, loading ->
            if (loading) emptyList()
            else preparations.filter { it.sellerId == sellerId }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1200)
            _isLoading.value = false
        }
    }
}
