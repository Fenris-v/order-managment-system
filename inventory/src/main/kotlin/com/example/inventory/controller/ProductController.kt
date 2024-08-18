package com.example.inventory.controller

import com.example.inventory.dto.response.product.ProductListResponse
import com.example.inventory.dto.response.product.ProductResponseWithCategories
import com.example.inventory.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер для работы с продуктами
 */
@RestController
class ProductController(private val productService: ProductService) {
    /**
     * Возвращает продукт по его идентификатору
     * @param productId идентификатор продукта
     * @return продукт
     */
    @GetMapping("/products/{productId}")
    fun getProductById(@PathVariable productId: Long): Mono<ProductResponseWithCategories> {
        return productService.getProductById(productId)
    }

    /**
     * Возвращает список продуктов по категории
     * @param slug название категории
     * @param page номер страницы
     * @param perPage количество продуктов на странице
     * @return список продуктов
     */
    @GetMapping("/categories/{slug}/products")
    fun getProductsByCategory(
        @PathVariable slug: String,
        @RequestParam("page") page: Int,
        @RequestParam("perPage") perPage: Int = 10
    ): Mono<ProductListResponse> {
        return productService.getProductsByCategory(slug, page, perPage)
    }
}
