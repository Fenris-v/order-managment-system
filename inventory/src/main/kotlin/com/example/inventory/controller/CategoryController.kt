package com.example.inventory.controller

import com.example.inventory.dto.response.category.CategoriesResponse
import com.example.inventory.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер для работы с категориями
 */
@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {
    /**
     * Метод для получения списка категорий
     * @return Mono<CategoriesResponse>
     */
    @GetMapping
    fun getCategories(): Mono<CategoriesResponse> {
        return categoryService.getAllCategories()
    }
}
