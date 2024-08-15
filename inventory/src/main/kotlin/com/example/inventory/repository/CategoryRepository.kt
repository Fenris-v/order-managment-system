package com.example.inventory.repository

import com.example.inventory.model.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Интерфейс репозитория для работы с данными категорий.
 */
interface CategoryRepository : ReactiveMongoRepository<Category, UUID> {
    /**
     * Проверяет, существует ли категория с указанным именем.
     * @param name Имя категории.
     */
    fun existsByName(name: String): Mono<Boolean>

    /**
     * Проверяет, существует ли хотя бы одна категория.
     */
    fun existsBy(): Mono<Boolean>
}
