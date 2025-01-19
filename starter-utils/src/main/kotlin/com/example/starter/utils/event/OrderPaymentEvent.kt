package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import java.util.UUID

/**
 * Данные события оплаты заказа.
 *
 * @param orderId Идентификатор заказа
 * @param status Статус заказа
 * @param products Список продуктов в заказе
 */
data class OrderPaymentEvent(
    val orderId: UUID? = null,
    val status: Status? = null,
    val products: List<OrderProduct>? = null
)
