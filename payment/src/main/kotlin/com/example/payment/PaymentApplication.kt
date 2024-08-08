package com.example.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Основной класс микросервиса Payment.
 */
@SpringBootApplication(scanBasePackages = ["com.example.payment", "com.example.starter.utils"])
class PaymentApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}
