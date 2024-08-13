package com.example.inventory.scheduler

import com.example.inventory.model.Category
import com.example.inventory.model.Product
import com.example.inventory.repository.CategoryRepository
import com.example.inventory.repository.ProductRepository
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.DBRef
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

private val log: KLogger = KotlinLogging.logger {}

@Component
class ProductSync(
    private val webClient: WebClient,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {
    @Scheduled(cron = "*/10 * * * * *")
    fun sync() {
        val categories: List<Category>? = categoryRepository.findAll().collectList().block()
        val categoryMap: MutableMap<String, UUID> = HashMap()
        categories?.forEach { categoryMap[it.slug] = it.id }

        syncProducts(categoryMap).then().subscribe()
    }

    private fun syncProducts(categoryMap: Map<String, UUID>): Mono<Void> {
        return webClient.get()
            .uri("https://dummyjson.com/products")
            .retrieve()
            .bodyToMono(ProductDataResponse::class.java)
            .flatMapMany { Flux.fromIterable(it.products) }
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap { processProduct(it, categoryMap) }
            .then()
    }

    private fun processProduct(productResponse: ProductResponse, categoryMap: Map<String, UUID>): Mono<Void> {
        return productRepository
            .findById(productResponse.id)
            .flatMap { product ->
                product.title = product.title
                product.price = product.price
                product.categories = getCategoriesForProduct(productResponse, categoryMap)

                productRepository.save(product)
            }
            .switchIfEmpty(createProduct(productResponse, categoryMap))
            .onErrorResume {
                log.error(it) { it.message }
                Mono.error(it)
            }
            .then()
    }

    private fun createProduct(productResponse: ProductResponse, categoryMap: Map<String, UUID>): Mono<Product> {
        val product = Product(
            productResponse.id,
            productResponse.title,
            productResponse.price,
            getCategoriesForProduct(productResponse, categoryMap)
        )

        return productRepository.save(product)
    }

    private fun getCategoriesForProduct(productResponse: ProductResponse, categoryMap: Map<String, UUID>): List<DBRef> {
        val categories: MutableList<DBRef> = ArrayList()
        if (categoryMap.containsKey(productResponse.category)) {
            categories.add(DBRef("categories", categoryMap[productResponse.category]!!))
        }

        return categories
    }
}

data class ProductDataResponse(
    @JsonProperty("products") val products: List<ProductResponse>,
    @JsonProperty("total") val total: Int,
    @JsonProperty("skip") val skip: Int,
    @JsonProperty("limit") val limit: Int
)

data class ProductResponse(
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("price") val price: Double,
    @JsonProperty("category") val category: String
)
