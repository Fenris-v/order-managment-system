package com.example.inventory.repository

import com.example.inventory.model.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Интерфейс репозитория для работы с данными о продуктах.
 */
interface ProductRepository : ReactiveMongoRepository<Product, Long> {

    /**
     * Проверяет, существует ли хотя бы один продукт.
     * @return true, если существует хотя бы один продукт, false в противном случае.
     */
    fun existsBy(): Mono<Boolean>

    /**
     * Поиск продуктов по категории.
     *
     * @param categoryId Идентификатор категории.
     * @return Список продуктов.
     */
    @Query("{'categories.\$id': ?0}")
    fun findAllByCategoryId(categoryId: UUID, pageable: Pageable): Flux<Product>

    /**
     * Количество продуктов по категории.
     *
     * @param categoryId Идентификатор категории.
     * @return Количество продуктов.
     */
    @Query("{'categories.\$id': ?0}", count = true)
    fun countByCategoriesId(categoryId: UUID): Mono<Long>
}
