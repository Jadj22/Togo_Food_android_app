package com.example.togofood.data.repository

import com.example.togofood.data.mock.MockData
import com.example.togofood.domain.model.Category

interface CategoryRepository {
    fun getAll(): List<Category>
}

class MockCategoryRepository : CategoryRepository {
    override fun getAll(): List<Category> = MockData.categories
}
