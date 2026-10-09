package com.example.togofood.ui.screens.preparation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.MockPreparationRepository
import com.example.togofood.data.repository.MockSellerRepository
import com.example.togofood.data.repository.PreparationRepository
import com.example.togofood.data.repository.SellerRepository
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.ui.components.OptionSelection
import com.example.togofood.ui.components.RequestOptionGroup
import com.example.togofood.ui.components.RequestOptions
import com.example.togofood.ui.components.buildRequestMessage
import com.example.togofood.ui.components.formatFcfa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PreparationDetailUiState {
    object Loading : PreparationDetailUiState
    data class Success(
        val prep: Preparation,
        val seller: Seller?,
        val related: List<Preparation> = emptyList()
    ) : PreparationDetailUiState
    object NotFound : PreparationDetailUiState
    data class Error(val message: String) : PreparationDetailUiState
}

/** Deux façons de préparer sa demande : cliquer des choix, ou écrire librement. */
enum class RequestMode { CHOOSE, WRITE }

/**
 * État de la « demande personnalisée » (PRD : pas une commande, juste un message au vendeur).
 *
 * @param selections    options choisies, par identifiant de groupe.
 * @param message       texte qui sera envoyé : généré à partir des choix tant que
 *                      l'utilisateur ne l'a pas modifié lui-même.
 * @param isMessageEdited vrai dès que l'utilisateur a tapé dans le message ; les choix
 *                      ne l'écrasent alors plus (jusqu'à `resetMessage()`).
 */
data class CustomRequestState(
    val groups: List<RequestOptionGroup> = emptyList(),
    val mode: RequestMode = RequestMode.CHOOSE,
    val selections: Map<String, Set<String>> = emptyMap(),
    val message: String = "",
    val isMessageEdited: Boolean = false
) {
    val choiceCount: Int get() = selections.values.sumOf { it.size }
    val isPersonalized: Boolean get() = choiceCount > 0 || isMessageEdited
}

class PreparationDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: PreparationRepository = MockPreparationRepository()
    private val sellerRepository: SellerRepository = MockSellerRepository()

    private val preparationId: String = checkNotNull(savedStateHandle["preparationId"])

    private val _uiState = MutableStateFlow<PreparationDetailUiState>(PreparationDetailUiState.Loading)
    val uiState: StateFlow<PreparationDetailUiState> = _uiState.asStateFlow()

    /** Index de la variante choisie (UI uniquement, aucune commande). */
    private val _selectedVariant = MutableStateFlow(0)
    val selectedVariant: StateFlow<Int> = _selectedVariant.asStateFlow()

    /**
     * Demande personnalisée. Volontairement un seul StateFlow mis à jour de façon
     * synchrone (pas de combine/stateIn) : le champ texte lit `message` directement,
     * et un détour asynchrone ferait sauter le curseur ou perdre des caractères.
     */
    private val _request = MutableStateFlow(CustomRequestState())
    val request: StateFlow<CustomRequestState> = _request.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            CatalogRepository.preparations.collect { refreshIfLoaded() }
        }
        viewModelScope.launch {
            CatalogRepository.sellers.collect { refreshIfLoaded() }
        }
    }

    private fun refreshIfLoaded() {
        if (_uiState.value is PreparationDetailUiState.Success) {
            load(preserveRequest = true)
        }
    }

    fun selectVariant(index: Int) {
        _selectedVariant.value = index
        refreshMessage() // le libellé et le prix de la portion font partie du message
    }

    fun retry() = load()

    // ───────────── Demande personnalisée ─────────────

    fun setRequestMode(mode: RequestMode) {
        _request.update { it.copy(mode = mode) }
    }

    /**
     * Groupe à choix unique : choisir remplace, re-toucher l'option choisie la décoche.
     * Groupe à choix multiple : ajoute ou retire.
     */
    fun toggleOption(groupId: String, optionId: String) {
        _request.update { state ->
            val group = state.groups.firstOrNull { it.id == groupId } ?: return@update state
            val current = state.selections[groupId].orEmpty()
            val next = when {
                optionId in current -> current - optionId
                group.selection == OptionSelection.SINGLE -> setOf(optionId)
                else -> current + optionId
            }
            state.copy(selections = state.selections + (groupId to next))
        }
        refreshMessage()
    }

    /** L'utilisateur modifie le message à la main : on arrête de le régénérer. */
    fun onMessageChange(text: String) {
        _request.update { it.copy(message = text, isMessageEdited = true) }
    }

    /** Revient au message généré à partir des choix. */
    fun resetMessage() {
        _request.update { it.copy(isMessageEdited = false) }
        refreshMessage()
    }

    /** Message à envoyer ; si l'utilisateur a tout effacé, on retombe sur le message de base. */
    fun messageToSend(): String {
        val state = _request.value
        if (state.message.isNotBlank()) return state.message
        val prep = currentPrep ?: return ""
        return buildMessage(prep, state.copy(selections = emptyMap()))
    }

    // ───────────── Interne ─────────────

    private val currentPrep: Preparation?
        get() = (_uiState.value as? PreparationDetailUiState.Success)?.prep

    private fun refreshMessage() {
        val prep = currentPrep ?: return
        _request.update { state ->
            if (state.isMessageEdited) state else state.copy(message = buildMessage(prep, state))
        }
    }

    private fun buildMessage(prep: Preparation, state: CustomRequestState): String {
        val variant = prep.variants.getOrNull(_selectedVariant.value) ?: prep.variants.firstOrNull()
        return buildRequestMessage(
            dishName = prep.name,
            variantLabel = variant?.label,
            priceLabel = variant?.let { "${it.priceFcfa.formatFcfa()} FCFA" },
            groups = state.groups,
            selections = state.selections
        )
    }

    private fun resetRequest() {
        _request.value = CustomRequestState(
            groups = currentPrep?.let { RequestOptions.groupsFor(it) }.orEmpty()
        )
        refreshMessage()
    }

    private fun load(preserveRequest: Boolean = false) {
        if (!preserveRequest) {
            _uiState.value = PreparationDetailUiState.Loading
        }
        _uiState.value = try {
            val prep = repository.getById(preparationId)
            if (prep == null) {
                PreparationDetailUiState.NotFound
            } else {
                PreparationDetailUiState.Success(
                    prep = prep,
                    seller = sellerRepository.getById(prep.sellerId),
                    related = repository.getByCategory(prep.categoryId)
                        .filter { it.uuid != prep.uuid }
                        .sortedBy { it.distanceMeters }
                        .take(8)
                )
            }
        } catch (e: Exception) {
            PreparationDetailUiState.Error("Impossible de charger cette préparation.")
        }
        if (!preserveRequest) resetRequest()
    }
}