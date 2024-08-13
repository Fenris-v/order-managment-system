package com.example.inventory.repository

import com.example.inventory.model.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface CategoryRepository : ReactiveMongoRepository<Category, UUID> {
    fun existsByName(name: String): Mono<Boolean>
}
