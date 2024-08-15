package com.example.inventory.repository

import com.example.inventory.model.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

/**
 * Интерфейс репозитория для работы с данными о продуктах.
 */
interface ProductRepository : ReactiveMongoRepository<Product, Long> {
    /**
     * Проверяет, существует ли хотя бы один продукт.
     */
    fun existsBy(): Mono<Boolean>
}
