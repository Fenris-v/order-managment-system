package com.example.payment.consumer

import com.example.payment.exception.BalanceTooLowException
import com.example.payment.producer.PaymentEventProducer
import com.example.payment.repository.UserBalanceRepository
import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.event.OrderPaymentEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

@Component
class ItemsReservedEventConsumer(
    private val userBalanceRepository: UserBalanceRepository,
    private val paymentEventProducer: PaymentEventProducer
) {
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @KafkaListener(topics = ["\${spring.kafka.topic.inventory}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: ItemsReservedEvent): Mono<Void> {
        log.info { "Получено событие: $event" }
        val price: Int = calculateTotalPrice(event.products!!)

        return userBalanceRepository.findUserBalanceByUserId(event.userId!!)
            .switchIfEmpty(Mono.error(BalanceTooLowException()))
            .flatMap { balance ->
                if (balance.amount!! >= price) {
                    balance.amount = balance.amount!! - price
                    userBalanceRepository.save(balance)
                } else {
                    Mono.error(BalanceTooLowException())
                }
            }
            .flatMap { paymentEventProducer.send(makeOrderPaymentEvent(event, Status.PAID)) }
            .onErrorResume {
                log.error(it) { it.message }
                paymentEventProducer.send(makeOrderPaymentEvent(event, Status.PAYMENT_FAILED))
            }
    }

    private fun calculateTotalPrice(products: List<OrderProduct>): Int {
        return products.sumOf { (it.price ?: 0) * (it.amount ?: 0) }
    }

    private fun makeOrderPaymentEvent(event: ItemsReservedEvent, status: Status): OrderPaymentEvent {
        return OrderPaymentEvent(event.orderId, status, event.products)
    }
}
