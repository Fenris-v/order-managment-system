package com.example.order

import net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Основной класс микросервиса Order.
 */
@SpringBootApplication(
    scanBasePackages = ["com.example.order", "com.example.starter.utils"],
    exclude = [GrpcClientMetricAutoConfiguration::class]
)
class OrderApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
