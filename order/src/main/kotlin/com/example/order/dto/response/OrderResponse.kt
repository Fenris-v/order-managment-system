package com.example.order.dto.response

import com.example.starter.utils.enums.Status
import java.util.UUID

data class OrderResponse(val orderId: UUID, val status: Status)
