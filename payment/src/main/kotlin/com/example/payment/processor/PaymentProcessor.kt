package com.example.payment.processor

import com.example.payment.enums.UPaymentStatus
import com.example.payment.event.PaymentEvent
import com.example.payment.service.UMoneyPaymentService
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

@Component
class PaymentProcessor(private val UMoneyPaymentService: UMoneyPaymentService) {
    private var running: Boolean = true
    private val delayQueue: DelayQueue<PaymentEvent> = DelayQueue()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    @PostConstruct
    fun init() {
        executorService.submit(this::processTasks)
    }

    @PreDestroy
    fun shutdown() {
        running = false
        executorService.shutdown()
    }

    fun submitTask(event: PaymentEvent) {
        delayQueue.offer(event)
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

            UMoneyPaymentService.getTransactionStatus(event.paymentId)
                .flatMap { response ->
                    log.info { "Актуальный статус транзакции ${response.id}: ${response.status}" }
                    when (response.status) {
                        UPaymentStatus.CANCELED -> UMoneyPaymentService.cancelTransaction(event.paymentId)
                        UPaymentStatus.SUCCEEDED -> UMoneyPaymentService.processSucceedTransaction(response.id)
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
            return UMoneyPaymentService.cancelTransaction(event.paymentId)
        }
    }
}
