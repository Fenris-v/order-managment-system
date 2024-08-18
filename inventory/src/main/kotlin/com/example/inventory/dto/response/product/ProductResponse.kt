package com.example.inventory.dto.response.product

/**
 * DTO с данными о продукте.
 */
data class ProductResponse(
    var id: Long? = null,
    var title: String? = null,
    var price: Int? = null,
    var stock: Int? = null
)
