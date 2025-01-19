package com.example.order.controller

import com.example.order.dto.request.OrderRequest
import com.example.order.dto.response.OrderDetailResponse
import com.example.order.dto.response.OrderResponse
import com.example.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Контроллер для работы с заказами.
 */
@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    /**
     * Создает новый заказ.
     *
     * @param orderRequest Запрос на создание заказа.
     * @param authorization Заголовок авторизации.
     * @return Ответ с информацией о созданном заказе.
     */
    @PostMapping
    fun createOrder(
        @Valid @RequestBody orderRequest: OrderRequest,
        @RequestHeader authorization: String
    ): Mono<OrderResponse> {
        return orderService.createOrder(orderRequest, authorization)
    }

    /**
     * Получает список заказов пользователя.
     *
     * @param authorization Заголовок авторизации.
     * @return Список заказов пользователя.
     */
    @GetMapping
    fun getOrders(@RequestHeader authorization: String): Mono<List<OrderResponse>> {
        return orderService.getOrders(authorization)
    }

    /**
     * Получает информацию о заказе.
     *
     * @param authorization Заголовок авторизации.
     * @param orderId Идентификатор заказа.
     * @return Информация о заказе.
     */
    @GetMapping("/{orderId}")
    fun getOrders(
        @RequestHeader authorization: String,
        @PathVariable orderId: UUID
    ): Mono<OrderDetailResponse> {
        return orderService.getOrder(authorization, orderId)
    }
}
