package com.example.inventory.handler.event

import com.example.inventory.exception.ReservedException
import com.example.inventory.model.Product
import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import com.example.starter.utils.event.ItemsReservedEvent
import com.example.starter.utils.event.OrderRegisteredEvent
import com.example.starter.utils.handler.event.EventHandler
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

@Component
class OrderRegisteredEventHandler(private val mongoTemplate: ReactiveMongoTemplate) :
    EventHandler<OrderRegisteredEvent, ItemsReservedEvent> {

    @Transactional
    override fun handleEvent(event: OrderRegisteredEvent): Mono<ItemsReservedEvent> {
        log.info { "Получено событие: $event" }
        val productMap: Map<Long, Int> = event.products.associate { it.productId!! to it.amount!! }
        val productIds: List<Long> = productMap.keys.toList()

        return mongoTemplate.find(query(where("id").`in`(productIds)), Product::class.java)
            .switchIfEmpty(Mono.error(RuntimeException()))
            .collectList()
            .flatMap { products ->
                val productList: MutableList<OrderProduct> = ArrayList()
                products.forEach { product ->
                    if (product.stock < productMap[product.id]!!) {
                        return@flatMap Mono.error(ReservedException())
                    } else {
                        product.stock -= productMap[product.id]!!
                        productList.add(OrderProduct(product.id, productMap[product.id]!!, product.price))
                    }
                }

                Mono.`when`(products.map { mongoTemplate.save(it) })
                    .then(Mono.just(ItemsReservedEvent(event.orderId, Status.INVENTED, productList)))
            }
            .onErrorResume {
                if (it is ReservedException) log.info { it.message } else log.error(it) { it.message }
                Mono.just(ItemsReservedEvent(event.orderId, Status.INVENTORY_FAILED, ArrayList()))
            }
    }
}
