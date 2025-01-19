package com.example.delivery.processor

import com.example.delivery.event.DeliveryEvent
import com.example.delivery.producer.DeliveryProducer
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderDeliveredEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component
import java.util.concurrent.DelayQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для обработки задач доставки заказа.
 *
 * @param deliveryProducer Компонент для отправки сообщений о доставке
 */
@Component
class DeliveryProcessor(private val deliveryProducer: DeliveryProducer) {

    private var running: Boolean = true
    private val delayQueue: DelayQueue<DeliveryEvent> = DelayQueue()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Пост-конструктор для инициализации компонента. Добавляет задачу на доставку заказа в очередь.
     */
    @PostConstruct
    fun init() {
        executorService.submit(this::processTasks)
    }

    /**
     * Пред-деструктор для завершения компонента. Останавливает выполнение задач доставки заказа.
     */
    @PreDestroy
    fun shutdown() {
        running = false
        executorService.shutdown()
    }

    /**
     * Добавляет задачу на доставку заказа в очередь.
     *
     * @param event Задача на доставку заказа
     */
    fun submitTask(event: DeliveryEvent) {
        delayQueue.put(event)
        log.info { "Задача на доставку добавлена в очередь" }
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

    private fun createTask(event: DeliveryEvent): Runnable {
        return Runnable {
            log.info { "Заказ доставлен, отправка сообщения о доставке" }
            deliveryProducer.send(OrderDeliveredEvent(event.orderId, Status.DELIVERED))
                .subscribe()
        }
    }
}
