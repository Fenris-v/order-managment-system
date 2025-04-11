package com.example.order.model

import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

/**
 * Модель заказа.
 */
@Document(collection = "orders")
data class Order(
    @Id val id: UUID,
    val userId: Long,
    var status: Status,
    var products: List<OrderProduct>,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
