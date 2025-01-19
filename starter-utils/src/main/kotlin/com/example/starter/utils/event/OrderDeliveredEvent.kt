package com.example.starter.utils.event

import com.example.starter.utils.enums.Status
import java.util.UUID

/**
 * Класс представляет событие доставки заказа.
 *
 * @param orderId Идентификатор заказа
 * @param status Статус заказа
 */
data class OrderDeliveredEvent(val orderId: UUID? = null, val status: Status? = null)
