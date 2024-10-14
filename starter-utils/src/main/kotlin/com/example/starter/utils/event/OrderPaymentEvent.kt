package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import java.util.UUID

data class OrderPaymentEvent(
    val orderId: UUID? = null,
    val status: Status? = null,
    val products: List<OrderProduct>? = null
)
