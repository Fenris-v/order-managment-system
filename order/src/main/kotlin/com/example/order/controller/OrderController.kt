package com.example.order.controller

import com.example.order.dto.request.OrderRequest
import com.example.order.dto.response.OrderResponse
import com.example.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    @PostMapping
    fun createOrder(
        @Valid @RequestBody orderRequest: OrderRequest,
        @RequestHeader authorization: String
    ): Mono<OrderResponse> {
        return orderService.createOrder(orderRequest, authorization)
    }
}
