package com.example.inventory.service

import com.example.inventory.dto.response.category.CategoriesResponse
import com.example.inventory.dto.response.category.CategoryResponse
import com.example.inventory.repository.CategoryRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Сервис для работы с данными категорий.
 */
@Service
class CategoryService(private val categoryRepository: CategoryRepository, private val modelMapper: ModelMapper) {
    /**
     * Возвращает список категорий.
     * @return Mono<CategoriesResponse>
     */
    fun getAllCategories(): Mono<CategoriesResponse> {
        return categoryRepository.findAllByOrderByName()
            .flatMapSequential { Mono.just(modelMapper.map(it, CategoryResponse::class.java)) }.collectList()
            .map { CategoriesResponse(it) }
    }
}
