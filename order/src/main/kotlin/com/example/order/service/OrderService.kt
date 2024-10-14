package com.example.order.service

import com.example.order.dto.request.OrderRequest
import com.example.order.dto.response.OrderResponse
import com.example.order.mapper.OrderMapper
import com.example.order.producer.OrderProcessor
import com.example.order.repository.OrderRepository
import com.example.starter.utils.utils.jwt.JwtUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderProcessor: OrderProcessor,
    private val orderMapper: OrderMapper,
    private val jwtUtils: JwtUtils
) {
    fun createOrder(orderRequest: OrderRequest, authorization: String): Mono<OrderResponse> {
        return Mono.just(jwtUtils.extractAllClaims(authorization).id)
            .flatMap { userId -> Mono.just(orderMapper.mapOrderRequestToOrder(orderRequest, userId)) }
            .flatMap { order ->
                orderRepository.save(order)
                    .flatMap { Mono.just(orderProcessor.process(it)) }
                    .map { OrderResponse(order.id, order.status) }
            }
    }
}
