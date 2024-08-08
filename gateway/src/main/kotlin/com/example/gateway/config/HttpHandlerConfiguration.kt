package com.example.gateway.config

import com.example.gateway.config.filter.NonGlobalFilter
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.web.server.WebFilter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder

/**
 * Конфигурация обработчика HTTP-запросов.
 */
@Configuration
class HttpHandlerConfiguration {
    /**
     * Создает и настраивает обработчик HTTP-запросов.
     *
     * @param context Контекст приложения Spring.
     * @return Обработчик HTTP-запросов.
     */
    @Bean
    fun httpHandler(context: ApplicationContext): HttpHandler {
        return WebHttpHandlerBuilder
            .applicationContext(context)
            .filters { filters: MutableList<WebFilter?> ->
                filters.removeIf { webFilter: WebFilter? -> webFilter is NonGlobalFilter }
            }
            .build()
    }
}
