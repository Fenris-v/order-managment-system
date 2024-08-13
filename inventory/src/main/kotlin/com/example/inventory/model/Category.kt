package com.example.inventory.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "categories")
data class Category(
    @Id val id: UUID,
    val name: String,
    val slug: String
)
