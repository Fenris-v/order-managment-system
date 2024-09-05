package com.example.starter.utils.config

import org.modelmapper.ModelMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = ["starter.utils.web-client"], havingValue = "true")
    fun webClient(): WebClient {
        return WebClient.create()
    }

    /**
     * Экземпляр [ModelMapper].
     *
     * @return [ModelMapper]
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = ["starter.utils.model-mapper"], havingValue = "true")
    fun modelMapper(): ModelMapper {
        return ModelMapper()
    }
}
