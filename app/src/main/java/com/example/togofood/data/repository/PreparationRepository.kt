package com.example.togofood.data.repository

import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.Preparation
import kotlinx.coroutines.flow.StateFlow

interface PreparationRepository {
    fun getAll(): List<Preparation>
    fun getNearby(): List<Preparation>
    fun getAvailableNow(): List<Preparation>
    fun getPopular(): List<Preparation>
    fun getByCategory(categoryId: String): List<Preparation>
    fun getBySellerId(sellerId: String): List<Preparation>
    fun search(query: String): List<Preparation>
    fun getById(id: String): Preparation?
    fun upsert(preparation: Preparation)
    fun updateAvailability(preparationId: String, available: Boolean)
    fun observeAll(): StateFlow<List<Preparation>>
}

class MockPreparationRepository : PreparationRepository {

    private val catalog: List<Preparation>
        get() = CatalogRepository.preparations.value

    override fun getAll(): List<Preparation> = catalog

    override fun getNearby(): List<Preparation> =
        catalog.sortedBy { it.distanceMeters }

    override fun getAvailableNow(): List<Preparation> =
        catalog.filter { it.availabilityStatus == AvailabilityStatus.AVAILABLE }
            .sortedBy { it.distanceMeters }

    override fun getPopular(): List<Preparation> =
        catalog.filter { it.isPopular }
            .sortedByDescending { it.rating }

    override fun getByCategory(categoryId: String): List<Preparation> =
        catalog.filter { it.categoryId == categoryId }

    override fun getBySellerId(sellerId: String): List<Preparation> =
        CatalogRepository.getPreparationsBySeller(sellerId)

    override fun search(query: String): List<Preparation> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return catalog
        return catalog.filter {
            it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.sellerName.lowercase().contains(q) ||
                it.sellerZone.lowercase().contains(q)
        }
    }

    override fun getById(id: String): Preparation? = CatalogRepository.getPreparation(id)

    override fun upsert(preparation: Preparation) = CatalogRepository.upsertPreparation(preparation)

    override fun updateAvailability(preparationId: String, available: Boolean) =
        CatalogRepository.setPreparationAvailability(preparationId, available)

    override fun observeAll(): StateFlow<List<Preparation>> = CatalogRepository.preparations
}
