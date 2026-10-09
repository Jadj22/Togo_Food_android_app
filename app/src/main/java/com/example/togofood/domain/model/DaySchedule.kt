package com.example.togofood.domain.model

import java.util.Calendar

/**
 * Créneau horaire simple (heures entières, PRD §37).
 * [closeHour] est exclusif (ex. 8→20 = ouvert de 08h à 20h).
 */
data class TimeSlot(
    val openHour: Int,
    val closeHour: Int,
)

/**
 * Horaires d’un jour de la semaine ([dayOfWeek] = [Calendar.SUNDAY]…[Calendar.SATURDAY]).
 */
data class DaySchedule(
    val dayOfWeek: Int,
    val isOpen: Boolean,
    val slots: List<TimeSlot> = emptyList(),
) {
    companion object {
        /** Lun–Sam 8h–20h, dimanche fermé. */
        fun defaultWeek(): List<DaySchedule> =
            (Calendar.SUNDAY..Calendar.SATURDAY).map { day ->
                when (day) {
                    Calendar.SUNDAY -> DaySchedule(day, isOpen = false)
                    else -> DaySchedule(
                        dayOfWeek = day,
                        isOpen = true,
                        slots = listOf(TimeSlot(openHour = 8, closeHour = 20)),
                    )
                }
            }
    }
}

object SellerScheduleResolver {

    fun effectiveOpenStatus(
        seller: Seller,
        now: Calendar = Calendar.getInstance(),
    ): SellerOpenStatus {
        if (!seller.useSchedule) return seller.SellerOpenStatus
        return statusFromSchedule(seller.schedule, now)
    }

    fun statusFromSchedule(
        schedule: List<DaySchedule>,
        now: Calendar = Calendar.getInstance(),
    ): SellerOpenStatus {
        val today = schedule.find { it.dayOfWeek == now.get(Calendar.DAY_OF_WEEK) }
            ?: return SellerOpenStatus.CLOSED
        if (!today.isOpen || today.slots.isEmpty()) return SellerOpenStatus.CLOSED
        val minutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val openNow = today.slots.any { slot ->
            val start = slot.openHour.coerceIn(0, 23) * 60
            val end = slot.closeHour.coerceIn(0, 24) * 60
            minutes in start until end
        }
        return if (openNow) SellerOpenStatus.OPEN else SellerOpenStatus.CLOSED
    }

    /** Résumé compact pour la fiche publique (ex. « Lun–Sam 8h–20h · Dim fermé »). */
    fun summaryLines(schedule: List<DaySchedule>): List<String> {
        if (schedule.isEmpty()) return emptyList()
        val openDays = schedule.filter { it.isOpen && it.slots.isNotEmpty() }
        if (openDays.isEmpty()) return listOf("Fermé toute la semaine")

        val groups = linkedMapOf<String, MutableList<Int>>()
        for (day in openDays.sortedBy { isoOrder(it.dayOfWeek) }) {
            val key = day.slots.joinToString(", ") { slot ->
                "${slot.openHour}h–${slot.closeHour}h"
            }
            groups.getOrPut(key) { mutableListOf() }.add(day.dayOfWeek)
        }

        val lines = groups.map { (hours, days) ->
            "${formatDayRange(days)} $hours"
        }.toMutableList()

        val closed = schedule.filter { !it.isOpen }.map { it.dayOfWeek }
        if (closed.isNotEmpty()) {
            lines += "${formatDayRange(closed)} fermé"
        }
        return lines
    }

    private fun isoOrder(calendarDay: Int): Int = when (calendarDay) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 6
    }

    private fun shortLabel(calendarDay: Int): String = when (calendarDay) {
        Calendar.MONDAY -> "Lun"
        Calendar.TUESDAY -> "Mar"
        Calendar.WEDNESDAY -> "Mer"
        Calendar.THURSDAY -> "Jeu"
        Calendar.FRIDAY -> "Ven"
        Calendar.SATURDAY -> "Sam"
        Calendar.SUNDAY -> "Dim"
        else -> "?"
    }

    private fun formatDayRange(days: List<Int>): String {
        val ordered = days.distinct().sortedBy { isoOrder(it) }
        if (ordered.size == 1) return shortLabel(ordered.first())
        val consecutive = ordered.zipWithNext().all { (a, b) ->
            isoOrder(b) - isoOrder(a) == 1
        }
        return if (consecutive && ordered.size > 2) {
            "${shortLabel(ordered.first())}–${shortLabel(ordered.last())}"
        } else {
            ordered.joinToString(", ") { shortLabel(it) }
        }
    }
}
