package com.example.togofood.data.repository

import com.example.togofood.data.mock.MockData
import com.example.togofood.domain.model.AvailabilityStatus
import com.example.togofood.domain.model.DaySchedule
import com.example.togofood.domain.model.Preparation
import com.example.togofood.domain.model.Seller
import com.example.togofood.domain.model.SellerOpenStatus
import com.example.togofood.domain.model.SellerScheduleResolver
import com.example.togofood.domain.model.VendorStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Catalogue mutable partagé (vendeurs + préparations).
 * Les mocks et le hub vendeur écrivent ici ; Home / Search / SellerDetail lisent le même état.
 */
object CatalogRepository {

    private val _sellers = MutableStateFlow(MockData.sellers)
    val sellers: StateFlow<List<Seller>> = _sellers.asStateFlow()

    private val _preparations = MutableStateFlow(MockData.preparations)
    val preparations: StateFlow<List<Preparation>> = _preparations.asStateFlow()

    fun getSeller(uuid: String): Seller? = _sellers.value.find { it.uuid == uuid }

    fun getPreparation(uuid: String): Preparation? = _preparations.value.find { it.uuid == uuid }

    fun getPreparationsBySeller(sellerId: String): List<Preparation> =
        _preparations.value.filter { it.sellerId == sellerId }

    fun upsertSeller(seller: Seller) {
        _sellers.update { list ->
            val idx = list.indexOfFirst { it.uuid == seller.uuid }
            if (idx >= 0) list.toMutableList().also { it[idx] = seller }
            else list + seller
        }
    }

    fun upsertPreparation(preparation: Preparation) {
        _preparations.update { list ->
            val idx = list.indexOfFirst { it.uuid == preparation.uuid }
            if (idx >= 0) list.toMutableList().also { it[idx] = preparation }
            else list + preparation
        }
    }

    fun deletePreparation(preparationId: String) {
        _preparations.update { list -> list.filterNot { it.uuid == preparationId } }
    }

    /**
     * Met à jour le profil boutique et synchronise [Preparation.sellerName] /
     * [Preparation.sellerZone] sur les plats du vendeur.
     */
    fun updateSellerProfile(seller: Seller) {
        upsertSeller(seller)
        _preparations.update { list ->
            list.map { prep ->
                if (prep.sellerId == seller.uuid) {
                    prep.copy(sellerName = seller.name, sellerZone = seller.zone)
                } else {
                    prep
                }
            }
        }
    }

    /**
     * Toggle ouverture boutique (PRD §37) :
     * override manuel — prime sur le planning.
     */
    fun setShopOpen(sellerId: String, open: Boolean) {
        val current = getSeller(sellerId) ?: return
        upsertSeller(
            current.copy(
                vendorStatus = if (open) VendorStatus.ACTIVE else VendorStatus.CLOSED,
                SellerOpenStatus = if (open) SellerOpenStatus.OPEN else SellerOpenStatus.CLOSED,
                useSchedule = false,
            )
        )
    }

    /** Enregistre les horaires et active le mode planning. */
    fun updateSellerSchedule(sellerId: String, schedule: List<DaySchedule>) {
        val current = getSeller(sellerId) ?: return
        val status = SellerScheduleResolver.statusFromSchedule(schedule)
        upsertSeller(
            current.copy(
                schedule = schedule,
                useSchedule = true,
                SellerOpenStatus = status,
                vendorStatus = if (status == SellerOpenStatus.OPEN) {
                    VendorStatus.ACTIVE
                } else {
                    VendorStatus.CLOSED
                },
            )
        )
    }

    fun setPreparationAvailability(preparationId: String, available: Boolean) {
        val current = getPreparation(preparationId) ?: return
        upsertPreparation(
            current.copy(
                availabilityStatus = if (available) {
                    AvailabilityStatus.AVAILABLE
                } else {
                    AvailabilityStatus.UNAVAILABLE
                }
            )
        )
    }

    /** Crée une boutique à partir de l’onboarding vendeur complet. */
    fun createSellerFromRegistration(
        shopName: String,
        description: String,
        categoryLabel: String,
        zone: String,
        landmark: String,
        phone: String,
        ownerName: String,
        schedule: List<DaySchedule> = DaySchedule.defaultWeek(),
        useSchedule: Boolean = true,
    ): Seller {
        val id = "s_owned_${System.currentTimeMillis()}"
        val resolvedZone = zone.ifBlank { "Lomé" }
        val resolvedLandmark = landmark.ifBlank { "À préciser" }
        val resolvedDescription = description.trim().ifBlank {
            buildString {
                append(categoryLabel.ifBlank { "Nourriture locale" })
                if (ownerName.isNotBlank()) append(" — géré par $ownerName")
            }
        }
        val openStatus = if (useSchedule) {
            SellerScheduleResolver.statusFromSchedule(schedule)
        } else {
            SellerOpenStatus.OPEN
        }
        val seller = Seller(
            uuid = id,
            name = shopName.ifBlank { "Ma boutique" },
            description = resolvedDescription,
            phone = if (phone.startsWith("+")) phone else "+228$phone",
            photoUrl = "",
            vendorStatus = if (openStatus == SellerOpenStatus.OPEN) {
                VendorStatus.ACTIVE
            } else {
                VendorStatus.CLOSED
            },
            zone = resolvedZone,
            landmark = resolvedLandmark,
            rating = 0f,
            reviewCount = 0,
            isVerified = false,
            distanceMeters = 200,
            SellerOpenStatus = openStatus,
            schedule = schedule.ifEmpty { DaySchedule.defaultWeek() },
            useSchedule = useSchedule,
        )
        upsertSeller(seller)
        return seller
    }
}
