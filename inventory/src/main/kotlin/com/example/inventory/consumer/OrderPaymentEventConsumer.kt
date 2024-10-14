package com.example.inventory.consumer

import com.example.inventory.model.Product
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.OrderPaymentEvent
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

@Component
class OrderPaymentEventConsumer(private val mongoTemplate: ReactiveMongoTemplate) {
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.payment}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderPaymentEvent): Mono<Void> {
        if (event.status == Status.PAYMENT_FAILED) {
            log.info { "Произошла ошибка оплаты, возврат товара на склад" }
            return returnItemsToStock(event)
        }

        return Mono.empty()
    }

    private fun returnItemsToStock(event: OrderPaymentEvent): Mono<Void> {
        val productMap: Map<Long, Int> = event.products!!.associate { it.productId!! to it.amount!! }
        val productIds: List<Long> = productMap.keys.toList()

        return mongoTemplate.find(Query.query(Criteria.where("id").`in`(productIds)), Product::class.java)
            .switchIfEmpty(Mono.error(RuntimeException()))
            .collectList()
            .flatMap { Mono.just(it.forEach { product -> product.stock += productMap[product.id]!! }).then() }
            .onErrorResume { returnItemsToStock(event) } // TODO: Можно доработать и сделать ретрай спустя время, а не засорять стэк рекурсивными вызовами
    }
}
