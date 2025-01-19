package com.example.gateway.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурационный класс для настройки Swagger.
 */
@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Gateway API",
        version = "1.0.0",
        description = "Документация для микросервиса управления пользователем."
    )
)
class SwaggerConfig(
    @Value("\${springdoc.domain}") private val domain: String,
    @Value("\${springdoc.prefix}") private val prefix: String
) {

    /**
     * Создает и настраивает экземпляр OpenAPI для использования с Swagger.
     *
     * @return Экземпляр OpenAPI
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        val server = Server()
        server.url = domain + prefix

        return OpenAPI().servers(listOf(server))
    }
}
