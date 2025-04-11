package com.example.inventory.controller

import com.example.inventory.dto.response.category.CategoriesResponse
import com.example.inventory.service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список категорий",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CategoriesResponse::class)
                )]
            )
        ]
    )
    @Operation(summary = "Получение списка категорий", description = "Получение списка категорий")
    fun getCategories(): Mono<CategoriesResponse> {
        return categoryService.getAllCategories()
    }
}
