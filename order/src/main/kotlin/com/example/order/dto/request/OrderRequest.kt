package com.example.order.dto.request

import com.example.starter.utils.dto.order.OrderProduct
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class OrderRequest(@field:Size(min = 1) @field:NotNull var products: List<OrderProduct>? = null)
