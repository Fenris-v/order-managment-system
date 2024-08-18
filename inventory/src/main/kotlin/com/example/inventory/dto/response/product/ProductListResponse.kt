package com.example.inventory.dto.response.product

import com.example.inventory.dto.response.category.CategoryResponse

/**
 * DTO для отображения списка продуктов.
 */
data class ProductListResponse(
    val products: List<ProductResponse>,
    val category: CategoryResponse,
    val totalPages: Long,
    val total: Long,
    val page: Int
)
