package com.example.inventory.repository

import com.example.inventory.model.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Интерфейс репозитория для работы с данными категорий.
 */
interface CategoryRepository : ReactiveMongoRepository<Category, UUID> {

    /**
     * Проверяет, существует ли категория с указанным именем.
     * @param name Имя категории.
     * @return true, если категория с указанным именем существует.
     */
    fun existsByName(name: String): Mono<Boolean>

    /**
     * Проверяет, существует ли хотя бы одна категория.
     * @return true, если хотя бы одна категория существует.
     */
    fun existsBy(): Mono<Boolean>

    /**
     * Возвращает все категории, отсортированные по алфавиту.
     * @return Список категорий.
     */
    fun findAllByOrderByName(): Flux<Category>

    /**
     * Возвращает все категории по списку идентификаторов, отсортированные по алфавиту.
     * @return Список категорий.
     */
    fun findAllByIdInOrderByName(ids: List<UUID>): Flux<Category>

    /**
     * Возвращает категорию по её слагу.
     * @return Категория.
     */
    fun findBySlug(slug: String): Mono<Category>
}
