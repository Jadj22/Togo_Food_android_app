package com.example.togofood.ui.screens.sellerhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.R
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SellerProfileEditUiState(
    val name: String = "",
    val description: String = "",
    val phoneDigits: String = "",
    val zone: String = "",
    val landmark: String = "",
    val nameError: Int? = null,
    val phoneError: Int? = null,
    val missingSession: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
)

class SellerProfileEditViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SellerProfileEditUiState())
    val uiState: StateFlow<SellerProfileEditUiState> = _uiState.asStateFlow()

    private var sellerId: String? = null

    init {
        val session = SessionRepository.session.value
        val id = session.sellerId
        if (!session.isVendor || id.isNullOrBlank()) {
            _uiState.update { it.copy(missingSession = true) }
        } else {
            sellerId = id
            val seller = CatalogRepository.getSeller(id)
            if (seller == null) {
                _uiState.update { it.copy(missingSession = true) }
            } else {
                _uiState.update {
                    it.copy(
                        name = seller.name,
                        description = seller.description,
                        phoneDigits = seller.phone.filter { c -> c.isDigit() }.takeLast(8),
                        zone = seller.zone,
                        landmark = seller.landmark,
                    )
                }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun updatePhone(value: String) {
        val digits = value.filter { it.isDigit() }.take(8)
        _uiState.update { it.copy(phoneDigits = digits, phoneError = null) }
    }

    fun updateZone(value: String) {
        _uiState.update { it.copy(zone = value) }
    }

    fun updateLandmark(value: String) {
        _uiState.update { it.copy(landmark = value) }
    }

    fun save() {
        val id = sellerId ?: return
        val current = CatalogRepository.getSeller(id) ?: return
        val state = _uiState.value
        var valid = true
        var nameError: Int? = null
        var phoneError: Int? = null

        if (state.name.isBlank()) {
            nameError = R.string.error_shop_name_required
            valid = false
        }
        if (state.phoneDigits.length != 8) {
            phoneError = R.string.error_phone_invalid
            valid = false
        }
        if (!valid) {
            _uiState.update { it.copy(nameError = nameError, phoneError = phoneError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            CatalogRepository.updateSellerProfile(
                current.copy(
                    name = state.name.trim(),
                    description = state.description.trim(),
                    phone = "+228${state.phoneDigits}",
                    zone = state.zone.trim().ifBlank { current.zone },
                    landmark = state.landmark.trim().ifBlank { current.landmark },
                )
            )
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }
}
