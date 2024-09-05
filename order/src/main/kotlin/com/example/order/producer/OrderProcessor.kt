package com.example.order.producer

import com.example.order.model.Order
import com.example.starter.utils.event.OrderRegisteredEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

private val log: KLogger = KotlinLogging.logger {}

@Component
class OrderProcessor(private val sink: Sinks.Many<OrderRegisteredEvent>) {
    fun process(order: Order) {
        val orderRegisteredEvent = OrderRegisteredEvent(order.id, order.products)
        sink.emitNext(orderRegisteredEvent, Sinks.EmitFailureHandler.FAIL_FAST)
        log.info { "Отправлено событие: $order" }
    }
}
