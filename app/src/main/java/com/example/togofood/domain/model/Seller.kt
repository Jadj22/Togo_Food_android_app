package com.example.togofood.domain.model

data class Seller(
    val uuid: String,
    val name: String,
    val description: String,
    val phone: String,
    val photoUrl: String,
    val vendorStatus: VendorStatus,
    val zone: String,
    val landmark: String,
    val rating: Float,
    val reviewCount: Int,
    val isVerified: Boolean,
    val distanceMeters: Int,
    val SellerOpenStatus: SellerOpenStatus,
    /** Planning hebdo (PRD §35 / §37). */
    val schedule: List<DaySchedule> = DaySchedule.defaultWeek(),
    /**
     * Si true, [SellerOpenStatus] est dérivé du planning ;
     * sinon le toggle hub (Ouvert/Fermé) prime.
     */
    val useSchedule: Boolean = false,
)

enum class VendorStatus { ACTIVE, CLOSED, SUSPENDED }

enum class SellerOpenStatus { OPEN,
    CLOSED,
    OPENING_SOON
}

