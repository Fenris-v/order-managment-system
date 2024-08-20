package com.example.order.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderRequest(@field:Min(1) @field:NotNull var products: List<OrderProduct>? = null)

data class OrderProduct(
    @field:NotNull
    var productId: Long? = null,

    @field:Min(1)
    @field:NotNull
    var amount: Int? = null
)
