package com.example.starter.utils.handler.event

import com.example.starter.utils.event.SagaEvent
import reactor.core.publisher.Mono

interface EventConsumer<T : SagaEvent> {
    fun consumeEvent(event: T): Mono<Void>
}
