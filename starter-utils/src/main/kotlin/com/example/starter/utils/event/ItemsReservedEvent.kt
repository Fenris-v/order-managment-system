package com.example.starter.utils.event

import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import java.util.UUID

data class ItemsReservedEvent(val orderId: UUID, val status: Status, val products: List<OrderProduct>) : SagaEvent
