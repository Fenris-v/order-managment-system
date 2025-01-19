package com.example.payment.listener

import com.example.payment.event.PaymentEvent
import com.example.payment.processor.PaymentProcessor
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val log: KLogger = KotlinLogging.logger {}

/**
 * Слушатель событий платежей.
 * <p>
 * Этот слушатель принимает события платежей и передает их в процессор платежей для проверки статуса.
 */
@Component
class PaymentStatusListener(private val paymentProcessor: PaymentProcessor) {

    /**
     * Метод слушает события платежей и передает их в процессор платежей для проверки статуса.
     *
     * @param event Событие платежа
     */
    @EventListener
    fun onApplicationEvent(event: PaymentEvent) {
        paymentProcessor.submitTask(event)
        log.info { "Событие отправлено в процессор платежей" }
    }
}
