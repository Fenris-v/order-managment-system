package com.example.order.producer

import com.example.order.model.Order
import com.example.starter.utils.event.OrderRegisteredEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

private val log: KLogger = KotlinLogging.logger {}

/**
 * Процессор для отправки событий о создании заказа.
 */
@Component
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderRegisteredEvent>,
    @Value("\${spring.kafka.topic.orders}") private val topic: String
) {

    /**
     * Отправляет событие о создании заказа.
     *
     * @param order Заказ
     */
    fun process(order: Order) {
        val orderRegisteredEvent = OrderRegisteredEvent(order.id, order.userId, order.products)
        kafkaTemplate.send(topic, orderRegisteredEvent)
        log.info { "Отправлено событие: $order" }
    }
}
