package com.example.inventory.service

import com.example.inventory.dto.response.category.CategoryResponse
import com.example.inventory.dto.response.product.ProductListResponse
import com.example.inventory.dto.response.product.ProductResponse
import com.example.inventory.dto.response.product.ProductResponseWithCategories
import com.example.inventory.mapper.ProductMapper
import com.example.inventory.model.Product
import com.example.inventory.repository.CategoryRepository
import com.example.inventory.repository.ProductRepository
import com.example.starter.utils.exception.EntityNotFoundException
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.UUID

/**
 * Сервис для работы с продуктами.
 */
@Service
class ProductService(
    private val modelMapper: ModelMapper,
    private val productMapper: ProductMapper,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {

    /**
     * Метод для получения продукта по идентификатору.
     * @param id Идентификатор продукта.
     * @return Mono<ProductResponseWithCategories>
     */
    fun getProductById(id: Long): Mono<ProductResponseWithCategories> {
        return productRepository
            .findById(id)
            .cache(Duration.ofMinutes(10))
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .flatMap { product ->
                categoryRepository.findAllByIdInOrderByName(product.categories.map { it.id as UUID }.toList())
                    .cache(Duration.ofMinutes(10))
                    .collectList()
                    .map { categories -> (productMapper.mapProductToProductResponse(product, categories)) }
            }
    }

    /**
     * Метод для получения списка продуктов по категории.
     * @param categorySlug Слаг категории.
     * @param page Номер страницы.
     * @param perPage Количество продуктов на странице.
     * @return Mono<ProductListResponse>
     */
    fun getProductsByCategory(categorySlug: String, page: Int, perPage: Int): Mono<ProductListResponse> {
        return categoryRepository.findBySlug(categorySlug)
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .flatMap { categoryModel ->
                val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Order.asc("_id")))
                val productsFlux: Flux<Product> = productRepository.findAllByCategoryId(categoryModel.id, pageable)

                productsFlux.map { product -> modelMapper.map(product, ProductResponse::class.java) }
                    .collectList()
                    .zipWith(productRepository.countByCategoriesId(categoryModel.id))
                    .map { tuple ->
                        val totalPages = (tuple.t2 / perPage) + if (tuple.t2 % perPage == 0L) 0 else 1
                        val category: CategoryResponse = modelMapper.map(categoryModel, CategoryResponse::class.java)

                        ProductListResponse(tuple.t1, category, totalPages, tuple.t2, page)
                    }
            }
    }

    fun getProductsByIdList(idsList: List<Long>): Flux<Product> {
        return productRepository.findAllById(idsList)
    }
}
