package com.example.order.mapper

import com.example.order.dto.request.OrderRequest
import com.example.order.model.Order
import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.enums.Status
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class OrderMapper(private val modelMapper: ModelMapper) {
    fun mapOrderRequestToOrder(orderRequest: OrderRequest, userId: Long): Order {
        return Order(
            UUID.randomUUID(),
            userId,
            Status.REGISTERED,
            orderRequest.products!!.map { orderProduct -> modelMapper.map(orderProduct, OrderProduct::class.java) },
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    }
}
