package com.example.starter.utils.handler.event

import com.example.starter.utils.event.SagaEvent
import reactor.core.publisher.Mono

interface EventHandler<T : SagaEvent, R : SagaEvent> {
    fun handleEvent(event: T): Mono<R>
}
