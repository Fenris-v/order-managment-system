package com.example.delivery.producer

import com.example.starter.utils.event.OrderDeliveredEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeliveryProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderDeliveredEvent>,
    @Value("\${spring.kafka.topic.delivery}") private val topic: String
) {
    fun send(event: OrderDeliveredEvent): Mono<Void> {
        kafkaTemplate.send(topic, event)
        return Mono.empty()
    }
}
