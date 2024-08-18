package com.example.inventory.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

/**
 * Сущность категории.
 */
@Document(collection = "categories")
data class Category(
    @Id val id: UUID,
    @Indexed(unique = true) val name: String,
    @Indexed(unique = true) val slug: String
)
