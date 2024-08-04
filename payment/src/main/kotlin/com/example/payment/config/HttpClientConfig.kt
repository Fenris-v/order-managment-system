package com.example.payment.config

import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

/**
 * Конфигурация HTTP-клиента.
 */
@Configuration
class HttpClientConfig {
    /**
     * Создает и настраивает HTTP-клиент.
     *
     * @return HTTP-клиент.
     */
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
    }

    /**
     * Создает и настраивает WebClient.
     *
     * @return WebClient
     */
    @Bean
    fun client(): WebClient {
        return WebClient.create()
    }
}
