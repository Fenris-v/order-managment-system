package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderDeliveredEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик событий доставки.
 */
@Component
class OrderDeliveredEventConsumer(orderRepository: OrderRepository) : AbstractStatusEventConsumer(orderRepository) {

    /**
     * Обрабатывает событие доставки заказа.
     *
     * @param event Событие доставки заказа.
     */
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.delivery}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderDeliveredEvent): Mono<Unit> {
        log.info { "Получено событие о статусе доставки: ${event.orderId}" }
        return if (event.status == Status.DELIVERED) updateOrderStatus(event.status!!, event.orderId!!)
        else Mono.empty()
    }
}
