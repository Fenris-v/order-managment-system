package com.example.order.dto.response

import com.example.starter.utils.enums.Status
import java.util.UUID

/**
 * Объект, описывающий ответ на запрос создания заказа.
 */
data class OrderResponse(val orderId: UUID, val status: Status)
