package com.example.order.controller

import com.example.order.dto.request.OrderRequest
import com.example.order.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    @PostMapping
    fun createOrder(orderRequest: OrderRequest, @RequestHeader("Authorization") token: String): Mono<Void> {
        return orderService.createOrder(orderRequest, 1L)
    }
}
