package com.example.gateway.config.security.handler

import com.example.starter.utils.dto.response.ExceptionDto
import com.example.starter.utils.exception.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик исключений авторизации для JWT-аутентификации.
 * <p>
 * Этот обработчик предоставляет метод для обработки исключений, связанных с авторизацией, и формирования
 * соответствующего ответа с кодом состояния и сообщением об ошибке.
 */
@Component
class AuthorizationExceptionHandler(private val objectMapper: ObjectMapper) {

    /**
     * Обрабатывает исключение авторизации и формирует соответствующий ответ с кодом состояния и сообщением об ошибке.
     *
     * @param message  сообщение об ошибке
     * @param exchange объект ServerWebExchange, представляющий текущий обмен
     * @param status   код состояния HTTP, связанный с ошибкой авторизации
     * @return Mono, представляющий завершение обработки и формирование ответа
     */
    fun handleAuthorizationException(message: String, exchange: ServerWebExchange, status: HttpStatusCode): Mono<Unit> {
        try {
            val dto = ExceptionDto(message, status.value())
            val response = exchange.response

            response.headers.contentType = MediaType.APPLICATION_JSON
            response.setStatusCode(status)
            val dataBuffer: DataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(dto))

            return response.writeWith(Mono.just(dataBuffer)).thenReturn(Unit)
        } catch (ex: Exception) {
            log.error(ex) { "JWT exception" }
            throw JsonParseException()
        }
    }
}
