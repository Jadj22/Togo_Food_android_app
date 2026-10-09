package com.example.togofood.ui.screens.sellerhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.R
import com.example.togofood.data.mock.MockData
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Category
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Variant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VariantDraft(
    val id: String = UUID.randomUUID().toString(),
    val label: String = "",
    val priceText: String = "",
)

data class SellerPrepEditUiState(
    val isEdit: Boolean = false,
    val name: String = "",
    val description: String = "",
    val categoryId: String = MockData.categories.firstOrNull()?.id.orEmpty(),
    val categories: List<Category> = MockData.categories,
    val isPopular: Boolean = false,
    val isAvailable: Boolean = true,
    val variants: List<VariantDraft> = listOf(VariantDraft(label = "Portion", priceText = "")),
    val nameError: Int? = null,
    val variantsError: Int? = null,
    val missingSession: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val showDeleteConfirm: Boolean = false,
)

class SellerPrepEditViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val prepIdArg: String? = savedStateHandle.get<String>("prepId")
        ?.takeIf { it.isNotBlank() && it != "null" }

    private val _uiState = MutableStateFlow(SellerPrepEditUiState())
    val uiState: StateFlow<SellerPrepEditUiState> = _uiState.asStateFlow()

    private var existingUuid: String? = null
    private var sellerId: String? = null

    init {
        val session = SessionRepository.session.value
        val id = session.sellerId
        if (!session.isVendor || id.isNullOrBlank() || CatalogRepository.getSeller(id) == null) {
            _uiState.update { it.copy(missingSession = true) }
        } else {
            sellerId = id
            val existing = prepIdArg?.let { CatalogRepository.getPreparation(it) }
            if (existing != null && existing.sellerId == id) {
                existingUuid = existing.uuid
                _uiState.update {
                    it.copy(
                        isEdit = true,
                        name = existing.name,
                        description = existing.description,
                        categoryId = existing.categoryId,
                        isPopular = existing.isPopular,
                        isAvailable = existing.availabilityStatus == AvailabilityStatus.AVAILABLE,
                        variants = existing.variants.map { v ->
                            VariantDraft(label = v.label, priceText = v.priceFcfa.toString())
                        }.ifEmpty { listOf(VariantDraft(label = "Portion")) },
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

    fun updateCategory(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun updatePopular(value: Boolean) {
        _uiState.update { it.copy(isPopular = value) }
    }

    fun updateAvailable(value: Boolean) {
        _uiState.update { it.copy(isAvailable = value) }
    }

    fun updateVariantLabel(variantId: String, label: String) {
        _uiState.update { state ->
            state.copy(
                variants = state.variants.map {
                    if (it.id == variantId) it.copy(label = label) else it
                },
                variantsError = null,
            )
        }
    }

    fun updateVariantPrice(variantId: String, priceText: String) {
        val cleaned = priceText.filter { it.isDigit() }.take(7)
        _uiState.update { state ->
            state.copy(
                variants = state.variants.map {
                    if (it.id == variantId) it.copy(priceText = cleaned) else it
                },
                variantsError = null,
            )
        }
    }

    fun addVariant() {
        _uiState.update {
            it.copy(variants = it.variants + VariantDraft(), variantsError = null)
        }
    }

    fun removeVariant(variantId: String) {
        _uiState.update { state ->
            if (state.variants.size <= 1) state
            else state.copy(variants = state.variants.filterNot { it.id == variantId })
        }
    }

    fun save() {
        val sid = sellerId ?: return
        val seller = CatalogRepository.getSeller(sid) ?: return
        val state = _uiState.value

        var nameError: Int? = null
        var variantsError: Int? = null
        if (state.name.isBlank()) nameError = R.string.error_prep_name_required
        if (state.variants.isEmpty()) {
            variantsError = R.string.error_variant_required
        } else if (state.variants.any { it.label.isBlank() }) {
            variantsError = R.string.error_variant_label
        } else if (state.variants.any { (it.priceText.toIntOrNull() ?: 0) <= 0 }) {
            variantsError = R.string.error_variant_price
        }

        if (nameError != null || variantsError != null) {
            _uiState.update { it.copy(nameError = nameError, variantsError = variantsError) }
            return
        }

        val variants = state.variants.map {
            Variant(label = it.label.trim(), priceFcfa = it.priceText.toInt())
        }
        val uuid = existingUuid ?: "p_${System.currentTimeMillis()}"

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            CatalogRepository.upsertPreparation(
                Preparation(
                    uuid = uuid,
                    name = state.name.trim(),
                    description = state.description.trim(),
                    photoUrl = "",
                    categoryId = state.categoryId,
                    sellerId = seller.uuid,
                    sellerName = seller.name,
                    sellerZone = seller.zone,
                    availabilityStatus = if (state.isAvailable) {
                        AvailabilityStatus.AVAILABLE
                    } else {
                        AvailabilityStatus.UNAVAILABLE
                    },
                    variants = variants,
                    rating = 0f,
                    reviewCount = 0,
                    distanceMeters = seller.distanceMeters,
                    isPopular = state.isPopular,
                )
            )
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }

    fun requestDelete() {
        if (_uiState.value.isEdit) {
            _uiState.update { it.copy(showDeleteConfirm = true) }
        }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun confirmDelete() {
        val uuid = existingUuid ?: return
        viewModelScope.launch {
            CatalogRepository.deletePreparation(uuid)
            _uiState.update {
                it.copy(showDeleteConfirm = false, deleted = true)
            }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    fun consumeDeleted() {
        _uiState.update { it.copy(deleted = false) }
    }
}
