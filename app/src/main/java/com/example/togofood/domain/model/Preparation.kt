package com.example.togofood.domain.model

data class Preparation(
    val uuid: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val categoryId: String,
    val sellerId: String,
    val sellerName: String,
    val sellerZone: String,
    val availabilityStatus: AvailabilityStatus,
    val variants: List<Variant>,
    val rating: Float,
    val reviewCount: Int,
    val distanceMeters: Int,
    val isPopular: Boolean = false,
    val recentPreparations: List<Preparation> = emptyList()
) {
    /** Lowest price variant for display on cards */
    val basePrice: Int get() = variants.minOfOrNull { it.priceFcfa } ?: 0
}
