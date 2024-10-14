package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import java.util.UUID

data class OrderRegisteredEvent(
    val orderId: UUID? = null,
    val userId: Long? = null,
    val products: List<OrderProduct>? = null
)
