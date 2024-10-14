package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import reactor.core.publisher.Mono
import java.util.UUID

abstract class AbstractStatusEventConsumer(protected val orderRepository: OrderRepository) {
    protected fun updateOrderStatus(status: Status, orderId: UUID): Mono<Void> {
        return orderRepository.findById(orderId).flatMap {
            it.status = status
            orderRepository.save(it).then()
        }
    }
}
