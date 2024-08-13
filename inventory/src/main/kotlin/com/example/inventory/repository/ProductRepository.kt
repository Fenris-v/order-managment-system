package com.example.inventory.repository

import com.example.inventory.model.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProductRepository : ReactiveMongoRepository<Product, Long>
