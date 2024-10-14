package com.example.payment.producer

import com.example.starter.utils.event.OrderPaymentEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PaymentEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderPaymentEvent>,
    @Value("\${spring.kafka.topic.payment}") private val topic: String
) {
    fun send(event: OrderPaymentEvent): Mono<Void> {
        kafkaTemplate.send(topic, event)
        return Mono.empty()
    }
}
