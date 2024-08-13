package com.example.inventory.utils.sync

import com.example.inventory.model.Category
import com.example.inventory.repository.CategoryRepository
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

@Component
class CategorySyncUtil(private val webClient: WebClient, private val categoryRepository: CategoryRepository) {
    fun syncCategories(): Mono<Void> {
        return webClient.get()
            .uri("https://dummyjson.com/products/categories")
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

data class CategoryResponse(@JsonProperty("slug") val slug: String, @JsonProperty("name") val name: String)
