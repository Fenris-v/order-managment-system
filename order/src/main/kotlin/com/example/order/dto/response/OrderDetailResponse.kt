package com.example.order.dto.response

import com.example.starter.utils.enums.Status
import java.time.LocalDateTime
import java.util.UUID

/**
 * Объект, описывающий ответ на получение детальной информации по заказу.
 */
data class OrderDetailResponse(
    val id: UUID,
    val userId: Long,
    var status: Status,
    var products: List<Product>,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)

/**
 * Товар в заказе с названием.
 */
data class Product(
    var productId: Long? = null,
    var title: String? = null,
    var amount: Int? = null,
    var price: Int? = null
)
