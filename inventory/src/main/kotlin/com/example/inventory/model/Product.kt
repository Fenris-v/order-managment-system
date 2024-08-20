package com.example.inventory.model

import com.mongodb.DBRef
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Сущность продукта.
 */
@Document("products")
class Product(
    @Id val id: Long,
    var title: String,
    var price: Int,
    var stock: Int,
    var categories: List<DBRef>
)
