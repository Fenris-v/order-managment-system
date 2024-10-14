package com.example.starter.utils.event

import com.example.starter.utils.enums.Status
import java.util.UUID

data class OrderDeliveredEvent(val orderId: UUID? = null, val status: Status? = null)
