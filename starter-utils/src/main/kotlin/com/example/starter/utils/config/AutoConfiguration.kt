package com.example.starter.utils.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Класс автоматической конфигурации стартера. Регистрирует бины стартера в зависимости от конфигураций приложения.
 */
@Configuration
@ConditionalOnProperty(name = ["starter.utils.enabled"], havingValue = "true", matchIfMissing = true)
class AutoConfiguration {
    /**
     * Создает и настраивает WebClient.
     *
     * @return WebClient
     */
    @Bean
    @ConditionalOnProperty(name = ["starter.utils.web-client"], havingValue = "true")
    fun webClient(): WebClient {
        return WebClient.create()
    }
}
