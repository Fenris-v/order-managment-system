package com.example.delivery.consumer

import com.example.delivery.event.DeliveryEvent
import com.example.delivery.processor.DeliveryProcessor
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderPaymentEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

@Component
class OrderPaymentEventConsumer(private val deliveryProcessor: DeliveryProcessor) {
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.payment}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderPaymentEvent): Mono<Void> {
        if (event.status == Status.PAID) {
            log.info { "Заказ оплачен, отправка заказа на доставку" }
            deliveryProcessor.submitTask(DeliveryEvent(event.orderId!!))
        }

        return Mono.empty()
    }
}
