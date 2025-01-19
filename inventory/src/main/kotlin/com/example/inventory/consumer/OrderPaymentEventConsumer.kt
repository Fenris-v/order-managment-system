package com.example.inventory.consumer

import com.example.inventory.model.Product
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderPaymentEvent
import com.example.starter.utils.exception.EntityNotFoundException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Класс для обработки событий оплаты заказа.
 */
@Component
class OrderPaymentEventConsumer(private val mongoTemplate: ReactiveMongoTemplate) {

    /**
     * Обработка событий оплаты заказа. В случае ошибки оплаты возвращает товар на склад.
     *
     * @param event Событие оплаты заказа.
     */
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.payment}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderPaymentEvent): Mono<Unit> {
        if (event.status == Status.PAYMENT_FAILED) {
            log.info { "Произошла ошибка оплаты, возврат товара на склад" }
            return returnItemsToStock(event).thenReturn(Unit)
        }

        return Mono.empty()
    }

    private fun returnItemsToStock(event: OrderPaymentEvent): Mono<Unit> {
        val productMap: Map<Long, Int> = event.products!!.associate { it.productId!! to it.amount!! }
        val productIds: List<Long> = productMap.keys.toList()

        return mongoTemplate.find(Query.query(Criteria.where("id").`in`(productIds)), Product::class.java)
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .collectList()
            .flatMap { products ->
                val updateOperations = products.map {
                    it.stock += productMap[it.id] ?: 0
                    mongoTemplate.save(it)
                }

                Flux.merge(updateOperations).then(Mono.just(Unit))
            }
    }
}
