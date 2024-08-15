package com.example.inventory.utils.sync

import com.example.inventory.model.Category
import com.example.inventory.repository.CategoryRepository
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

/**
 * Класс для синхронизации категорий.
 */
@Lazy
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class CategorySyncUtil(
    @Value("\${app.productSync.host}") private val host: String,
    @Value("\${app.productSync.scheme}") private val scheme: String,
    private val webClient: WebClient,
    private val categoryRepository: CategoryRepository
) {
    /**
     * Метод для синхронизации категорий.
     */
    fun syncCategories(): Mono<Void> {
        return webClient.get()
            .uri { it.scheme(scheme).host(host).path("/products/categories").build() }
            .retrieve()
            .bodyToFlux(CategoryResponse::class.java)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap { processCategory(it) }
            .then()
    }

    private fun processCategory(category: CategoryResponse): Mono<Void> {
        return categoryRepository
            .existsByName(category.name)
            .flatMap { exists ->
                if (exists) Mono.empty()
                else categoryRepository.save(Category(UUID.randomUUID(), category.name, category.slug))
            }
            .then()
    }
}

/**
 * Класс для хранения информации о категории.
 */
data class CategoryResponse(@JsonProperty("slug") val slug: String, @JsonProperty("name") val name: String)
