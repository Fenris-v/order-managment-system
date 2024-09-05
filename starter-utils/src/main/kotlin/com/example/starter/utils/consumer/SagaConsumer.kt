package com.example.starter.utils.consumer

import com.example.starter.utils.event.SagaEvent
import reactor.core.publisher.Mono

interface SagaConsumer<T : SagaEvent, R : SagaEvent> {
    fun handle(event: T): Mono<R>
}
