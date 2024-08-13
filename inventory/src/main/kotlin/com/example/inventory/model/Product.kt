package com.example.inventory.model

import com.mongodb.DBRef
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("products")
class Product(
    @Id val id: Long,
    var title: String,
    var price: Double,
    var categories: List<DBRef>
)
