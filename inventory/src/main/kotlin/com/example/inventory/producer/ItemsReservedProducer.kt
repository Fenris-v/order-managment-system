package com.example.inventory.producer

import com.example.starter.utils.event.ItemsReservedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Продюсер для отправки событий резервирования товаров.
 */
@Component
class ItemsReservedProducer(
    private val kafkaTemplate: KafkaTemplate<String, ItemsReservedEvent>,
    @Value("\${spring.kafka.topic.inventory}") private val topic: String
) {

    /**
     * Отправка событий резервирования товаров.
     *
     * @param event Событие
     */
    fun send(event: ItemsReservedEvent): Mono<Unit> {
        kafkaTemplate.send(topic, event)
        return Mono.empty()
    }
}
