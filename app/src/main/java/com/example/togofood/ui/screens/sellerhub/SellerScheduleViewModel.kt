package com.example.togofood.ui.screens.sellerhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togofood.data.repository.CatalogRepository
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.domain.model.DaySchedule
import com.example.togofood.domain.model.TimeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class SellerScheduleUiState(
    val days: List<DaySchedule> = DaySchedule.defaultWeek(),
    val missingSession: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
)

class SellerScheduleViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SellerScheduleUiState())
    val uiState: StateFlow<SellerScheduleUiState> = _uiState.asStateFlow()

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
                val days = seller.schedule.ifEmpty { DaySchedule.defaultWeek() }
                    .sortedBy { dayOrder(it.dayOfWeek) }
                _uiState.update { it.copy(days = days) }
            }
        }
    }

    fun setDayOpen(dayOfWeek: Int, open: Boolean) {
        _uiState.update { state ->
            state.copy(
                days = state.days.map { day ->
                    if (day.dayOfWeek != dayOfWeek) day
                    else if (open) {
                        day.copy(
                            isOpen = true,
                            slots = day.slots.ifEmpty { listOf(TimeSlot(8, 20)) },
                        )
                    } else {
                        day.copy(isOpen = false, slots = emptyList())
                    }
                }
            )
        }
    }

    fun updateSlotOpenHour(dayOfWeek: Int, slotIndex: Int, hour: Int) {
        updateSlot(dayOfWeek, slotIndex) { it.copy(openHour = hour.coerceIn(0, 23)) }
    }

    fun updateSlotCloseHour(dayOfWeek: Int, slotIndex: Int, hour: Int) {
        updateSlot(dayOfWeek, slotIndex) { it.copy(closeHour = hour.coerceIn(1, 24)) }
    }

    fun addSlot(dayOfWeek: Int) {
        _uiState.update { state ->
            state.copy(
                days = state.days.map { day ->
                    if (day.dayOfWeek != dayOfWeek || !day.isOpen || day.slots.size >= 2) day
                    else day.copy(slots = day.slots + TimeSlot(14, 18))
                }
            )
        }
    }

    fun removeSlot(dayOfWeek: Int, slotIndex: Int) {
        _uiState.update { state ->
            state.copy(
                days = state.days.map { day ->
                    if (day.dayOfWeek != dayOfWeek || day.slots.size <= 1) day
                    else day.copy(slots = day.slots.filterIndexed { i, _ -> i != slotIndex })
                }
            )
        }
    }

    fun save() {
        val id = sellerId ?: return
        val days = _uiState.value.days.map { day ->
            if (!day.isOpen) day.copy(slots = emptyList())
            else day.copy(
                slots = day.slots.map { slot ->
                    val open = slot.openHour.coerceIn(0, 23)
                    val close = slot.closeHour.coerceIn(open + 1, 24)
                    TimeSlot(open, close)
                }
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            CatalogRepository.updateSellerSchedule(id, days)
            _uiState.update { it.copy(isSaving = false, saved = true, days = days) }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    private fun updateSlot(
        dayOfWeek: Int,
        slotIndex: Int,
        transform: (TimeSlot) -> TimeSlot,
    ) {
        _uiState.update { state ->
            state.copy(
                days = state.days.map { day ->
                    if (day.dayOfWeek != dayOfWeek) day
                    else day.copy(
                        slots = day.slots.mapIndexed { i, slot ->
                            if (i == slotIndex) transform(slot) else slot
                        }
                    )
                }
            )
        }
    }

    companion object {
        fun dayOrder(calendarDay: Int): Int = when (calendarDay) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 6
        }
    }
}
