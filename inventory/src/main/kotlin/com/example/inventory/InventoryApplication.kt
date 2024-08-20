package com.example.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class InventoryApplication

fun main(args: Array<String>) {
    runApplication<InventoryApplication>(*args)
}
