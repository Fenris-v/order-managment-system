package com.example.payment.producer

import com.example.starter.utils.event.OrderPaymentEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Продюсер событий платежей.
 */
@Component
class PaymentEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderPaymentEvent>,
    @Value("\${spring.kafka.topic.payment}") private val topic: String
) {

    /**
     * Отправляет событие платежа в топик Kafka.
     *
     * @param event Событие платежа
     */
    fun send(event: OrderPaymentEvent): Mono<Unit> {
        kafkaTemplate.send(topic, event)
        return Mono.empty()
    }
}
