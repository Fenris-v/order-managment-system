package com.example.gateway.util

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * Класс для работы с HTTP-запросами.
 */
@Component
class HttpUtil(private val client: WebClient) {

    /**
     * Метод для выполнения GET-запроса.
     *
     * @param url URL-адрес запроса.
     * @param apiKey API-ключ.
     * @param clazz Класс объекта, возвращаемого запросом.
     * @return Монада с результатом запроса.
     */
    fun <T> get(url: String, apiKey: String, clazz: Class<T>): Mono<T> {
        return client.get()
            .uri(url)
            .header("X-Api-Key", apiKey)
            .retrieve()
            .bodyToMono(clazz)
    }
}
