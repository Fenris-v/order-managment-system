package com.example.order.service

import com.example.order.dto.request.OrderRequest
import com.example.order.enums.Status
import com.example.order.model.Order
import com.example.order.model.OrderProducts
import com.example.order.repository.OrderRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

@Service
class OrderService(private val orderRepository: OrderRepository, private val modelMapper: ModelMapper) {
    fun createOrder(orderRequest: OrderRequest, userId: Long): Mono<Void> {
        return Mono.just(
            Order(
                UUID.randomUUID(),
                userId,
                Status.REGISTERED,
                orderRequest.products!!.map { orderProduct ->
                    modelMapper.map(orderProduct, OrderProducts::class.java)
                },
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )
            .flatMap { orderRepository.save(it).then() }
    }
}
