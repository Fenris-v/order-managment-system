package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import java.util.UUID

/**
 * Класс представляет событие регистрации заказа.
 *
 * @param orderId Идентификатор заказа
 * @param userId Идентификатор пользователя
 * @param products Список продуктов
 */
data class OrderRegisteredEvent(
    val orderId: UUID? = null,
    val userId: Long? = null,
    val products: List<OrderProduct>? = null
)
