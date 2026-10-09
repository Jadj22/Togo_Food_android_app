package com.example.togofood.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _phoneError = MutableStateFlow<Int?>(null) // String resource ID for error
    val phoneError: StateFlow<Int?> = _phoneError.asStateFlow()

    private val _passwordError = MutableStateFlow<Int?>(null)
    val passwordError: StateFlow<Int?> = _passwordError.asStateFlow()

    fun updatePhone(newPhone: String) {
        _phone.update { newPhone }
        _phoneError.update { null } // Clear error on typing
    }

    fun updatePassword(newPassword: String) {
        _password.update { newPassword }
        _passwordError.update { null }
    }

    fun togglePasswordVisibility() {
        _passwordVisible.update { !it }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(onSuccess: () -> Unit) {
        if (!validateForm()) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(1200)
            resolveSession()
            _isLoading.value = false
            onSuccess()
        }
    }

    /**
     * Démo V1 : si le téléphone correspond à un vendeur mock (8 derniers chiffres),
     * connexion en tant que vendeur ; sinon client.
     */
    private fun resolveSession() {
        val digits = _phone.value
        val matchedSeller = CatalogRepository.sellers.value.find { seller ->
            seller.phone.filter { it.isDigit() }.takeLast(8) == digits
        }
        if (matchedSeller != null) {
            SessionRepository.loginVendor(
                displayName = matchedSeller.name,
                phone = digits,
                sellerId = matchedSeller.uuid,
            )
        } else {
            SessionRepository.loginClient(
                displayName = "Client",
                phone = digits,
            )
        }
    }

    fun validateForm(): Boolean {
        var isValid = true
        
        if (_phone.value.length != 8 || !_phone.value.all { it.isDigit() }) {
            _phoneError.value = com.example.togofood.R.string.error_phone_invalid
            isValid = false
        }
        
        if (_password.value.length < 6) {
            _passwordError.value = com.example.togofood.R.string.error_password_short
            isValid = false
        }
        
        return isValid
    }
}
