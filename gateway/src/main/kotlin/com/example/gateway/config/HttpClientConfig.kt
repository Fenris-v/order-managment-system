package com.example.gateway.config

import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
}
