package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.ItemsReservedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

/**
 * Класс, обрабатывающий событие о резервировании товаров в инвентаре.
 */
@Component
class ItemsReservedEventConsumer(orderRepository: OrderRepository) : AbstractStatusEventConsumer(orderRepository) {

    /**
     * Обрабатывает событие о резервировании товаров.
     *
     * @param event Событие о резервировании товаров.
     */
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.inventory}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: ItemsReservedEvent): Mono<Unit> {
        return when (event.status) {
            Status.INVENTED -> handleSuccessInvented(event)
            Status.INVENTORY_FAILED -> handleFailedInvented(event)
            else -> Mono.empty()
        }
    }

    private fun handleSuccessInvented(event: ItemsReservedEvent): Mono<Unit> {
        return orderRepository.findById(event.orderId!!).flatMap { order ->
            if (order.status == Status.REGISTERED) {
                order.status = event.status!!
            }

            order.products = event.products!!
            orderRepository.save(order)
                .thenReturn(Unit)
        }
    }

    private fun handleFailedInvented(event: ItemsReservedEvent): Mono<Unit> {
        return updateOrderStatus(event.status!!, event.orderId!!)
    }
}
