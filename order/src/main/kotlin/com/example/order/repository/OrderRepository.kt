package com.example.order.repository

import com.example.order.model.Order
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.util.UUID

interface OrderRepository : ReactiveMongoRepository<Order, UUID>
