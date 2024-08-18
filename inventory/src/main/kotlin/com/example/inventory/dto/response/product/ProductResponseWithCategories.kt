package com.example.inventory.dto.response.product

import com.example.inventory.dto.response.category.CategoryResponse

/**
 * DTO с данными о продукте с категориями.
 */
data class ProductResponseWithCategories(
    var id: Long,
    var title: String,
    var price: Int,
    var stock: Int,
    var categories: List<CategoryResponse>
)
