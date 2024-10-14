package com.example.payment.processor

import com.example.payment.enums.UPaymentStatus
import com.example.payment.event.PaymentEvent
import com.example.payment.service.UMoneyPaymentService
import com.example.starter.utils.exception.EntityNotFoundException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.concurrent.DelayQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для обработки событий платежей.
 * <p>
 * Этот процессор принимает события платежей и выполняет проверку статуса платежа в UMoney. При обновлении статуса
 * запускает обновление транзакции.
 */
@Component
class PaymentProcessor(private val uMoneyPaymentService: UMoneyPaymentService) {
    private var running: Boolean = true
    private val delayQueue: DelayQueue<PaymentEvent> = DelayQueue()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Инициализация процессора
     */
    @PostConstruct
    fun init() {
        executorService.submit(this::processTasks)
    }

    /**
     * Остановка процессора
     */
    @PreDestroy
    fun shutdown() {
        running = false
        executorService.shutdown()
    }

    /**
     * Метод добавляет задачу в очередь
     *
     * @param event Событие платежа
     */
    fun submitTask(event: PaymentEvent) {
        delayQueue.put(event)
        log.info { "Задача на проверку статуса транзакции добавлена в очередь" }
    }

    private fun processTasks() {
        while (running) {
            try {
                val task: Runnable = createTask(delayQueue.take())
                task.run()
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun createTask(event: PaymentEvent): Runnable {
        return Runnable {
            log.info { "Проверка статусов транзакций" }

            uMoneyPaymentService.getTransactionStatus(event.paymentId)
                .switchIfEmpty(Mono.error(EntityNotFoundException()))
                .flatMap { response ->
                    log.info { "Актуальный статус транзакции ${response.id}: ${response.status}" }
                    when (response.status) {
                        UPaymentStatus.CANCELED -> uMoneyPaymentService.cancelTransaction(event.paymentId)
                        UPaymentStatus.SUCCEEDED -> uMoneyPaymentService.processSucceedTransaction(response.id)
                        UPaymentStatus.PENDING, UPaymentStatus.WAITING_FOR_CAPTURE -> {
                            return@flatMap catchPendingStatus(event)
                        }
                    }
                }
                .subscribe()
        }
    }

    private fun catchPendingStatus(event: PaymentEvent): Mono<Void> {
        if (LocalDateTime.now().isBefore(event.expiryAt)) {
            submitTask(PaymentEvent(event.paymentId, event.delaySeconds, event.expiryAt))
            return Mono.empty()
        } else {
            return uMoneyPaymentService.cancelTransaction(event.paymentId)
        }
    }
}
