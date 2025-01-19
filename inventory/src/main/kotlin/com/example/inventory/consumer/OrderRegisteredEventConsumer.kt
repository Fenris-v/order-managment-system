package com.example.inventory.consumer

import com.example.inventory.exception.ReservedException
import com.example.inventory.model.Product
import com.example.inventory.producer.ItemsReservedProducer
import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.event.OrderRegisteredEvent
import com.example.starter.utils.exception.EntityNotFoundException
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

/**
 * Класс для обработки событий заказа.
 */
@Component
class OrderRegisteredEventConsumer(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val itemsReservedProducer: ItemsReservedProducer
) {

    /**
     * Обрабатывает событие заказа и выполняет резервирование товаров на складе.
     *
     * @param event Событие заказа
     */
    @Transactional
    @KafkaListener(topics = ["\${spring.kafka.topic.orders}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEvent(event: OrderRegisteredEvent): Mono<Unit> {
        log.info { "Получено событие: $event" }
        val productMap: Map<Long, Int> = event.products!!.associate { it.productId!! to it.amount!! }
        val productIds: List<Long> = productMap.keys.toList()

        return mongoTemplate.find(Query.query(Criteria.where("id").`in`(productIds)), Product::class.java)
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .collectList()
            .flatMap { products ->
                val productList: MutableList<OrderProduct> = ArrayList()
                products.forEach { product ->
                    if (product.stock < (productMap[product.id] ?: 0)) {
                        return@flatMap Mono.error(ReservedException())
                    }
                }

                products.forEach { product ->
                    product.stock -= productMap[product.id] ?: 0
                    productList.add(OrderProduct(product.id, productMap[product.id], product.price))
                }

                Mono.`when`(products.map { mongoTemplate.save(it) })
                    .then(itemsReservedProducer.send(makeItemsReservedEvent(event, Status.INVENTED, productList)))
            }
            .onErrorResume {
                if (it is ReservedException) log.info { it.message } else log.error(it) { it.message }
                itemsReservedProducer.send(makeItemsReservedEvent(event, Status.INVENTORY_FAILED, listOf()))
            }
    }

    private fun makeItemsReservedEvent(
        event: OrderRegisteredEvent,
        status: Status,
        products: List<OrderProduct>
    ): ItemsReservedEvent {
        return ItemsReservedEvent(event.orderId!!, status, event.userId, products)
    }
}
