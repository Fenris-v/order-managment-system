package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import java.util.UUID

/**
 * Класс представляющий событие о зарезервированных товарах.
 *
 * @property orderId идентификатор заказа
 * @property status статус заказа
 * @property userId идентификатор пользователя
 * @property products список продуктов
 */
data class ItemsReservedEvent(
    val orderId: UUID? = null,
    val status: Status? = null,
    val userId: Long? = null,
    val products: List<OrderProduct>? = null
)
