package com.example.order.config

import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.event.OrderRegisteredEvent
import com.example.starter.utils.handler.event.EventConsumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import java.util.function.Consumer
import java.util.function.Supplier

@Configuration
class SagaConfig(private val itemsReservedEventConsumer: EventConsumer<ItemsReservedEvent>) {
    @Bean
    fun sink(): Many<OrderRegisteredEvent> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }

    @Bean
    fun orderRegisteredEventPublisher(publisher: Many<OrderRegisteredEvent>): Supplier<Flux<OrderRegisteredEvent>> {
        return Supplier { publisher.asFlux() }
    }

    @Bean
    fun itemsReservedEventProcessor(): Consumer<ItemsReservedEvent> {
        return Consumer { event: ItemsReservedEvent -> itemsReservedEventConsumer.consumeEvent(event) }
    }
}
