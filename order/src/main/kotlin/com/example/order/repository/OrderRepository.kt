package com.example.order.repository

import com.example.order.model.Order
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Репозиторий для работы с заказами.
 */
interface OrderRepository : ReactiveMongoRepository<Order, UUID> {

    /**
     * Найти все заказы пользователя.
     *
     * @param userId Идентификатор пользователя
     * @return Список заказов
     */
    fun findAllByUserId(userId: Long): Flux<Order>

    /**
     * Найти заказ по идентификатору и идентификатору пользователя.
     *
     * @param orderId Идентификатор заказа
     * @param userId Идентификатор пользователя
     * @return Заказ
     */
    fun findFirstByIdAndUserId(orderId: UUID, userId: Long): Mono<Order>
}
