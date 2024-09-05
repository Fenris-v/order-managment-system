package com.example.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.order", "com.example.starter.utils"])
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
