package com.example.order.consumer

import com.example.order.repository.OrderRepository
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderPaymentEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик события оплаты.
 */
@Component
class OrderPaymentEventConsumer(orderRepository: OrderRepository) : AbstractStatusEventConsumer(orderRepository) {

    /**
     * Обрабатывает событие оплаты заказа.
     *
     * @param event Событие оплаты заказа.
     */
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.payment}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderPaymentEvent): Mono<Unit> {
        return when (event.status) {
            Status.PAID -> handleSuccessPayment(event)
            Status.PAYMENT_FAILED -> handleFailedPayment(event)
            else -> Mono.empty()
        }
    }

    private fun handleSuccessPayment(event: OrderPaymentEvent): Mono<Unit> {
        log.info { "Оплата заказа №${event.orderId} прошла, обновление статуса" }
        return updateOrderStatus(event.status!!, event.orderId!!)
    }

    private fun handleFailedPayment(event: OrderPaymentEvent): Mono<Unit> {
        log.info { "Оплата заказа №${event.orderId} не прошла" }
        return updateOrderStatus(event.status!!, event.orderId!!)
    }
}
