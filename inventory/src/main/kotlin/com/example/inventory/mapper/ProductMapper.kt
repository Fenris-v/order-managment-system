package com.example.inventory.mapper

import com.example.inventory.dto.response.category.CategoryResponse
import com.example.inventory.dto.response.product.ProductResponseWithCategories
import com.example.inventory.model.Category
import com.example.inventory.model.Product
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

/**
 * Маппер для работы с сущностью {@link Product} и её представлениями.
 */
@Component
class ProductMapper(private val modelMapper: ModelMapper) {

    /**
     * Метод для преобразования сущности {@link Product} в представление {@link ProductResponseWithCategories}.
     *
     * @param product Сущность {@link Product}.
     * @param categories Список сущностей {@link Category}.
     * @return {@link ProductResponseWithCategories}.
     */
    fun mapProductToProductResponse(product: Product, categories: List<Category>): ProductResponseWithCategories {
        return ProductResponseWithCategories(
            product.id,
            product.title,
            product.price,
            product.stock,
            categories.map { modelMapper.map(it, CategoryResponse::class.java) }.toList()
        )
    }
}
