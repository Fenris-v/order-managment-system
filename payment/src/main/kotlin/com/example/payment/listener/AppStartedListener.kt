package com.example.payment.listener

import com.example.payment.enums.UPaymentStatus
import com.example.payment.event.PaymentEvent
import com.example.payment.processor.PaymentProcessor
import com.example.payment.repository.TransactionRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Слушатель события старта приложения.
 */
@Component
class AppStartedListener(
    private val transactionRepository: TransactionRepository,
    private val paymentProcessor: PaymentProcessor
) : ApplicationListener<ApplicationReadyEvent> {

    /**
     * Метод для обработки события старта приложения. На старте приложения добавляет в очередь платежи, которые
     * находятся в статусе PENDING.
     *
     * @param event Событие старта приложения.
     */
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        transactionRepository.findTransactionsByStatus(UPaymentStatus.PENDING)
            .concatMap {
                Mono.fromCallable {
                    paymentProcessor.submitTask(PaymentEvent(it.paymentId!!, expiryAt = it.createdAt.plusHours(1)))
                }
            }
            .subscribe()
    }
}
