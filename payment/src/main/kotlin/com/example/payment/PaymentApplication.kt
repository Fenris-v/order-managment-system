package com.example.payment

import net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Основной класс микросервиса Payment.
 */
@SpringBootApplication(
    scanBasePackages = ["com.example.payment", "com.example.starter.utils"],
    exclude = [GrpcClientMetricAutoConfiguration::class]
)
class PaymentApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}
