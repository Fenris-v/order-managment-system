package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Абстрактный класс для слушателя сообщений связанных с заказами.
 */
abstract class AbstractStatusEventConsumer(protected val orderRepository: OrderRepository) {

    protected fun updateOrderStatus(status: Status, orderId: UUID): Mono<Unit> {
        return orderRepository.findById(orderId).flatMap {
            it.status = status
            orderRepository.save(it)
                .thenReturn(Unit)
        }
    }
}
