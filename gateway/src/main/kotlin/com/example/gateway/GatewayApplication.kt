package com.example.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Основной класс микросервиса Gateway, который также содержит Spring Security.
 */
@EnableScheduling
@SpringBootApplication
@EnableR2dbcRepositories
class GatewayApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
