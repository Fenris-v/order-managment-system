package com.example.inventory.utils.sync

import com.example.inventory.model.Product
import com.example.inventory.repository.CategoryRepository
import com.example.inventory.repository.ProductRepository
import com.example.inventory.utils.CurrencyConverter
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.DBRef
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers
import java.util.UUID

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для синхронизации продуктов.
 */
@Lazy
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class ProductSyncUtil(
    @Value("\${app.productSync.host}") private val host: String,
    @Value("\${app.productSync.scheme}") private val scheme: String,
    private val webClient: WebClient,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {
    /**
     * Метод для синхронизации продуктов.
     */
    fun syncProducts(): Mono<Void> {
        val categoryMap: MutableMap<String, UUID> = HashMap()

        return categoryRepository.findAll().collectList()
            .mapNotNull { categories -> categories?.forEach { categoryMap[it.slug] = it.id } }.flatMap {
                getPage().flatMapMany { response ->
                    val firstPageProcessing = parseProducts(response, categoryMap)

                    val remainingPagesProcessing =
                        Flux.fromIterable(getRemainingPages(response)).parallel().runOn(Schedulers.boundedElastic())
                            .flatMap { skip ->
                                getPage(skip, response.limit).flatMapMany { parseProducts(it, categoryMap) }
                            }

                    Flux.concat(firstPageProcessing, remainingPagesProcessing)
                }.then()
            }
    }

    private fun getPage(skip: Int = 0, limit: Int = 30): Mono<ProductDataResponse> {
        return webClient.get()
            .uri {
                val uriBuilder: UriBuilder = it.scheme(scheme).host(host).path("/products")
                if (skip > 0) {
                    uriBuilder.queryParam("skip", skip).queryParam("limit", limit)
                }

                uriBuilder.build()
            }
            .retrieve()
            .bodyToMono(ProductDataResponse::class.java)
    }

    private fun parseProducts(response: ProductDataResponse, categoryMap: Map<String, UUID>): ParallelFlux<Void> {
        return Flux.fromIterable(response.products).parallel().runOn(Schedulers.boundedElastic())
            .flatMap { processProduct(it, categoryMap) }
    }

    private fun processProduct(response: ProductResponse, categoryMap: Map<String, UUID>): Mono<Void> {
        return productRepository.findById(response.id).flatMap { product ->
            product.title = response.title
            product.price = CurrencyConverter.usdToRub(response.price)
            product.categories = getCategoriesForProduct(response, categoryMap)

            productRepository.save(product)
        }.switchIfEmpty(createProduct(response, categoryMap)).onErrorResume {
            log.error(it) { it.message }
            Mono.error(it)
        }.then()
    }

    private fun createProduct(productResponse: ProductResponse, categoryMap: Map<String, UUID>): Mono<Product> {
        val product = Product(
            productResponse.id,
            productResponse.title,
            CurrencyConverter.usdToRub(productResponse.price),
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

    private fun getRemainingPages(response: ProductDataResponse): MutableList<Int> {
        val remainingPages: MutableList<Int> = ArrayList()
        var skip = response.limit
        while (skip < response.total) {
            remainingPages.add(skip)
            skip += response.limit
        }

        return remainingPages
    }
}

/**
 * Класс для получения данных о продуктах.
 */
data class ProductDataResponse(
    @JsonProperty("products") val products: List<ProductResponse>,
    @JsonProperty("total") val total: Int,
    @JsonProperty("skip") val skip: Int,
    @JsonProperty("limit") val limit: Int
)

/**
 * Класс для получения данных о продукте.
 */
data class ProductResponse(
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("price") val price: Double,
    @JsonProperty("category") val category: String
)
