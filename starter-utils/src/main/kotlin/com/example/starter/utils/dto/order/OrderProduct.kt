package com.example.starter.utils.dto.order

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderProduct(
    @field:NotNull
    var productId: Long? = null,

    @field:Min(1)
    @field:NotNull
    var amount: Int? = null,
    var price: Int? = null
)
