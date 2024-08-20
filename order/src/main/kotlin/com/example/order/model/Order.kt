package com.example.order.model

import com.example.order.enums.Status
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document(collection = "orders")
data class Order(
    @Id val id: UUID,
    val userId: Long,
    var status: Status,
    var products: List<OrderProducts>,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)

data class OrderProducts(val productId: Long, val amount: Int)
