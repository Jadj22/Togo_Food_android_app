package com.example.togofood.data.repository

import com.example.togofood.domain.model.Seller
import kotlinx.coroutines.flow.StateFlow

interface SellerRepository {
    fun getAll(): List<Seller>
    fun getById(uuid: String): Seller?
    fun update(seller: Seller)
    fun observeAll(): StateFlow<List<Seller>>
}

class MockSellerRepository : SellerRepository {
    override fun getAll(): List<Seller> = CatalogRepository.sellers.value
    override fun getById(uuid: String): Seller? = CatalogRepository.getSeller(uuid)
    override fun update(seller: Seller) = CatalogRepository.upsertSeller(seller)
    override fun observeAll(): StateFlow<List<Seller>> = CatalogRepository.sellers
}
