package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.handler.event.EventConsumer
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class ItemsReservedEventConsumer(private val orderRepository: OrderRepository) : EventConsumer<ItemsReservedEvent> {
    @Transactional
    override fun consumeEvent(event: ItemsReservedEvent): Mono<Void> {
        return when (event.status) {
            Status.INVENTED -> handleSuccessInvented(event)
            Status.INVENTORY_FAILED -> handleFailedInvented(event)
            else -> Mono.empty()
        }
    }

    private fun handleSuccessInvented(event: ItemsReservedEvent): Mono<Void> {
        return orderRepository.findById(event.orderId).flatMap { order ->
            if (order.status == Status.REGISTERED) {
                order.status = event.status
            }

            order.products = event.products
            orderRepository.save(order).then()
        }
    }

    private fun handleFailedInvented(event: ItemsReservedEvent): Mono<Void> {
        return orderRepository.findById(event.orderId).flatMap { order ->
            order.status = event.status
            orderRepository.save(order).then()
        }
    }
}
