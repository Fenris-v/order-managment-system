package com.example.inventory

import net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Основной класс микросервиса Inventory.
 */
@SpringBootApplication(exclude = [GrpcServerMetricAutoConfiguration::class])
class InventoryApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    runApplication<InventoryApplication>(*args)
}
