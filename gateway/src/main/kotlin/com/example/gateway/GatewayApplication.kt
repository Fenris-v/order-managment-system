package com.example.gateway

import net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import reactor.core.publisher.Hooks

/**
 * Основной класс микросервиса Gateway, который также содержит Spring Security.
 */
@EnableScheduling
@EnableR2dbcRepositories
@SpringBootApplication(
    scanBasePackages = ["com.example.gateway", "com.example.starter.utils"],
    exclude = [GrpcServerMetricAutoConfiguration::class]
)
class GatewayApplication

/**
 * Запуск приложения
 *
 * @param args Аргументы, указанные при запуске приложения
 */
fun main(args: Array<String>) {
    Hooks.enableAutomaticContextPropagation()
    runApplication<GatewayApplication>(*args)
}
