package com.example.order.dto.request

import com.example.starter.utils.dto.order.OrderProduct
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * Объект, описывающий запрос на создание заказа.
 */
data class OrderRequest(@field:Size(min = 1) @field:NotNull var products: List<OrderProduct>? = null)
