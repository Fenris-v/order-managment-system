package com.example.delivery.producer

import com.example.starter.utils.event.OrderDeliveredEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Продюсер сообщений с информацией о доставке заказа.
 */
@Component
class DeliveryProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderDeliveredEvent>,
    @Value("\${spring.kafka.topic.delivery}") private val topic: String
) {

    /**
     * Отправка сообщения о доставке заказа.
     *
     * @param event Событие доставки заказа
     */
    fun send(event: OrderDeliveredEvent): Mono<Unit> {
        kafkaTemplate.send(topic, event)
        return Mono.empty()
    }
}
