package com.example.togofood.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.R
import com.example.togofood.data.mock.MockData
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.domain.model.DaySchedule
import com.example.togofood.domain.model.TimeSlot
import com.example.togofood.domain.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/** Onboarding vendeur : 5 étapes après choix du rôle. */
object VendorOnboarding {
    const val TOTAL_STEPS = 5
    const val STEP_ACCOUNT = 1
    const val STEP_SHOP = 2
    const val STEP_LOCATION = 3
    const val STEP_SCHEDULE = 4
    const val STEP_REVIEW = 5
}

class RegisterViewModel : ViewModel() {

    private val _selectedRole = MutableStateFlow(UserRole.CLIENT)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _vendorStep = MutableStateFlow(VendorOnboarding.STEP_ACCOUNT)
    val vendorStep: StateFlow<Int> = _vendorStep.asStateFlow()

    private val _showOtpSheet = MutableStateFlow(false)
    val showOtpSheet: StateFlow<Boolean> = _showOtpSheet.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _neighborhood = MutableStateFlow("")
    val neighborhood: StateFlow<String> = _neighborhood.asStateFlow()

    private val _acceptTerms = MutableStateFlow(false)
    val acceptTerms: StateFlow<Boolean> = _acceptTerms.asStateFlow()

    // Vendor — boutique
    private val _shopName = MutableStateFlow("")
    val shopName: StateFlow<String> = _shopName.asStateFlow()

    private val _shopDescription = MutableStateFlow("")
    val shopDescription: StateFlow<String> = _shopDescription.asStateFlow()

    private val _shopCategoryId = MutableStateFlow(MockData.categories.firstOrNull()?.id.orEmpty())
    val shopCategoryId: StateFlow<String> = _shopCategoryId.asStateFlow()

    // Vendor — localisation
    private val _shopZone = MutableStateFlow("")
    val shopZone: StateFlow<String> = _shopZone.asStateFlow()

    private val _shopLandmark = MutableStateFlow("")
    val shopLandmark: StateFlow<String> = _shopLandmark.asStateFlow()

    private val _shopFindHint = MutableStateFlow("")
    val shopFindHint: StateFlow<String> = _shopFindHint.asStateFlow()

    // Vendor — horaires
    private val _scheduleDays = MutableStateFlow(DaySchedule.defaultWeek())
    val scheduleDays: StateFlow<List<DaySchedule>> = _scheduleDays.asStateFlow()

    private val _openHour = MutableStateFlow(8)
    val openHour: StateFlow<Int> = _openHour.asStateFlow()

    private val _closeHour = MutableStateFlow(20)
    val closeHour: StateFlow<Int> = _closeHour.asStateFlow()

    // Errors
    private val _phoneError = MutableStateFlow<Int?>(null)
    val phoneError: StateFlow<Int?> = _phoneError.asStateFlow()

    private val _nameError = MutableStateFlow<Int?>(null)
    val nameError: StateFlow<Int?> = _nameError.asStateFlow()

    private val _shopNameError = MutableStateFlow<Int?>(null)
    val shopNameError: StateFlow<Int?> = _shopNameError.asStateFlow()

    private val _shopDescError = MutableStateFlow<Int?>(null)
    val shopDescError: StateFlow<Int?> = _shopDescError.asStateFlow()

    private val _zoneError = MutableStateFlow<Int?>(null)
    val zoneError: StateFlow<Int?> = _zoneError.asStateFlow()

    private val _landmarkError = MutableStateFlow<Int?>(null)
    val landmarkError: StateFlow<Int?> = _landmarkError.asStateFlow()

    private val _termsError = MutableStateFlow<Int?>(null)
    val termsError: StateFlow<Int?> = _termsError.asStateFlow()

    private val _scheduleError = MutableStateFlow<Int?>(null)
    val scheduleError: StateFlow<Int?> = _scheduleError.asStateFlow()

    private val _isOtpLoading = MutableStateFlow(false)
    val isOtpLoading: StateFlow<Boolean> = _isOtpLoading.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    val categories get() = MockData.categories

    fun updateRole(role: UserRole) {
        _selectedRole.update { role }
        _vendorStep.update { VendorOnboarding.STEP_ACCOUNT }
        clearFieldErrors()
    }

    fun updateName(newValue: String) {
        _name.update { newValue }
        _nameError.update { null }
    }

    fun updatePhone(newValue: String) {
        val digits = newValue.filter { it.isDigit() }.take(8)
        _phone.update { digits }
        _phoneError.update { null }
    }

    fun updateNeighborhood(newValue: String) = _neighborhood.update { newValue }
    fun updateAcceptTerms(newValue: Boolean) {
        _acceptTerms.update { newValue }
        _termsError.update { null }
    }

    fun updateShopName(newValue: String) {
        _shopName.update { newValue }
        _shopNameError.update { null }
    }

    fun updateShopDescription(newValue: String) {
        _shopDescription.update { newValue }
        _shopDescError.update { null }
    }

    fun updateShopCategoryId(id: String) = _shopCategoryId.update { id }

    fun updateShopZone(newValue: String) {
        _shopZone.update { newValue }
        _zoneError.update { null }
    }

    fun updateShopLandmark(newValue: String) {
        _shopLandmark.update { newValue }
        _landmarkError.update { null }
    }

    fun updateShopFindHint(newValue: String) = _shopFindHint.update { newValue }

    fun setDayOpen(dayOfWeek: Int, open: Boolean) {
        _scheduleError.update { null }
        _scheduleDays.update { days ->
            days.map { day ->
                if (day.dayOfWeek != dayOfWeek) day
                else if (open) {
                    day.copy(
                        isOpen = true,
                        slots = listOf(TimeSlot(_openHour.value, _closeHour.value)),
                    )
                } else {
                    day.copy(isOpen = false, slots = emptyList())
                }
            }
        }
    }

    fun updateOpenHour(hour: Int) {
        val h = hour.coerceIn(0, 22)
        _openHour.update { h }
        applyHoursToOpenDays()
    }

    fun updateCloseHour(hour: Int) {
        val h = hour.coerceIn(1, 24)
        _closeHour.update { h }
        applyHoursToOpenDays()
    }

    fun applySchedulePreset(preset: SchedulePreset) {
        _scheduleError.update { null }
        when (preset) {
            SchedulePreset.EVERY_DAY -> {
                _openHour.value = 8
                _closeHour.value = 20
                _scheduleDays.value = (Calendar.SUNDAY..Calendar.SATURDAY).map { day ->
                    DaySchedule(day, true, listOf(TimeSlot(8, 20)))
                }
            }
            SchedulePreset.MON_SAT -> {
                _openHour.value = 8
                _closeHour.value = 20
                _scheduleDays.value = DaySchedule.defaultWeek()
            }
            SchedulePreset.LUNCH_DINNER -> {
                _openHour.value = 11
                _closeHour.value = 21
                _scheduleDays.value = (Calendar.SUNDAY..Calendar.SATURDAY).map { day ->
                    if (day == Calendar.SUNDAY) DaySchedule(day, false)
                    else DaySchedule(day, true, listOf(TimeSlot(11, 21)))
                }
            }
        }
    }

    fun clearOtpError() {
        _otpError.update { null }
    }

    fun navigateBack(): Boolean {
        if (_selectedRole.value == UserRole.VENDEUR && _vendorStep.value > VendorOnboarding.STEP_ACCOUNT) {
            _vendorStep.update { it - 1 }
            clearFieldErrors()
            return true
        }
        return false
    }

    fun submitStep(onSuccess: () -> Unit = {}) {
        if (!validateCurrentStep()) return

        when {
            _selectedRole.value == UserRole.CLIENT -> {
                _showOtpSheet.update { true }
                _otpError.update { null }
            }
            _selectedRole.value == UserRole.VENDEUR &&
                _vendorStep.value == VendorOnboarding.STEP_ACCOUNT -> {
                _showOtpSheet.update { true }
                _otpError.update { null }
            }
            _selectedRole.value == UserRole.VENDEUR &&
                _vendorStep.value < VendorOnboarding.STEP_REVIEW -> {
                _vendorStep.update { it + 1 }
            }
            else -> {
                persistSession()
                onSuccess()
            }
        }
    }

    fun dismissOtpSheet() {
        _showOtpSheet.update { false }
        _otpError.update { null }
    }

    fun confirmOtp(otp: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isOtpLoading.update { true }
            _otpError.update { null }
            delay(1500)

            if (otp == "1234") {
                _showOtpSheet.update { false }
                if (_selectedRole.value == UserRole.VENDEUR &&
                    _vendorStep.value == VendorOnboarding.STEP_ACCOUNT
                ) {
                    _vendorStep.update { VendorOnboarding.STEP_SHOP }
                } else {
                    persistSession()
                    onSuccess()
                }
            } else {
                _otpError.update { "Code incorrect. Essayez 1234." }
            }
            _isOtpLoading.update { false }
        }
    }

    private fun applyHoursToOpenDays() {
        val open = _openHour.value
        val close = _closeHour.value.coerceAtLeast(open + 1)
        _scheduleDays.update { days ->
            days.map { day ->
                if (!day.isOpen) day
                else day.copy(slots = listOf(TimeSlot(open, close)))
            }
        }
    }

    private fun persistSession() {
        when (_selectedRole.value) {
            UserRole.CLIENT -> SessionRepository.loginClient(
                displayName = _name.value,
                phone = _phone.value,
            )
            UserRole.VENDEUR -> {
                val categoryLabel = categories
                    .find { it.id == _shopCategoryId.value }
                    ?.label
                    .orEmpty()
                val description = buildString {
                    append(_shopDescription.value.trim())
                    val hint = _shopFindHint.value.trim()
                    if (hint.isNotBlank()) {
                        if (isNotEmpty()) append("\n")
                        append(hint)
                    }
                }
                val seller = CatalogRepository.createSellerFromRegistration(
                    shopName = _shopName.value,
                    description = description,
                    categoryLabel = categoryLabel,
                    zone = _shopZone.value,
                    landmark = _shopLandmark.value,
                    phone = _phone.value,
                    ownerName = _name.value,
                    schedule = _scheduleDays.value,
                    useSchedule = true,
                )
                SessionRepository.loginVendor(
                    displayName = _shopName.value.ifBlank { _name.value },
                    phone = _phone.value,
                    sellerId = seller.uuid,
                )
            }
        }
    }

    private fun validateCurrentStep(): Boolean {
        clearFieldErrors()
        return when {
            _selectedRole.value == UserRole.CLIENT -> validateClient()
            _vendorStep.value == VendorOnboarding.STEP_ACCOUNT -> validateAccount()
            _vendorStep.value == VendorOnboarding.STEP_SHOP -> validateShop()
            _vendorStep.value == VendorOnboarding.STEP_LOCATION -> validateLocation()
            _vendorStep.value == VendorOnboarding.STEP_SCHEDULE -> validateSchedule()
            _vendorStep.value == VendorOnboarding.STEP_REVIEW -> validateReview()
            else -> true
        }
    }

    private fun validateSchedule(): Boolean {
        if (_scheduleDays.value.none { it.isOpen }) {
            _scheduleError.value = R.string.error_schedule_days
            return false
        }
        return true
    }

    private fun validateClient(): Boolean {
        var ok = true
        if (_name.value.isBlank()) {
            _nameError.value = R.string.error_required_field
            ok = false
        }
        if (_phone.value.length != 8) {
            _phoneError.value = R.string.error_phone_invalid
            ok = false
        }
        return ok
    }

    private fun validateAccount(): Boolean {
        var ok = true
        if (_name.value.isBlank()) {
            _nameError.value = R.string.error_required_field
            ok = false
        }
        if (_phone.value.length != 8) {
            _phoneError.value = R.string.error_phone_invalid
            ok = false
        }
        return ok
    }

    private fun validateShop(): Boolean {
        var ok = true
        if (_shopName.value.isBlank()) {
            _shopNameError.value = R.string.error_shop_name_required
            ok = false
        }
        if (_shopDescription.value.trim().length < 10) {
            _shopDescError.value = R.string.error_shop_desc_short
            ok = false
        }
        return ok
    }

    private fun validateLocation(): Boolean {
        var ok = true
        if (_shopZone.value.isBlank()) {
            _zoneError.value = R.string.error_required_field
            ok = false
        }
        if (_shopLandmark.value.isBlank()) {
            _landmarkError.value = R.string.error_required_field
            ok = false
        }
        return ok
    }

    private fun validateReview(): Boolean {
        if (!_acceptTerms.value) {
            _termsError.value = R.string.error_terms_required
            return false
        }
        return true
    }

    private fun clearFieldErrors() {
        _phoneError.value = null
        _nameError.value = null
        _shopNameError.value = null
        _shopDescError.value = null
        _zoneError.value = null
        _landmarkError.value = null
        _termsError.value = null
        _scheduleError.value = null
    }
}

enum class SchedulePreset { EVERY_DAY, MON_SAT, LUNCH_DINNER }
