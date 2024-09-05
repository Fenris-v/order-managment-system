package com.example.inventory.config

import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.event.OrderRegisteredEvent
import com.example.starter.utils.handler.event.EventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function

@Configuration
class SagaConfig(private val orderRegisteredEventHandler: EventHandler<OrderRegisteredEvent, ItemsReservedEvent>) {
    @Bean
    fun orderRegisteredEventProcessor(): Function<OrderRegisteredEvent, ItemsReservedEvent> {
        return Function { event: OrderRegisteredEvent -> orderRegisteredEventHandler.handleEvent(event).block()!! }
    }
}

