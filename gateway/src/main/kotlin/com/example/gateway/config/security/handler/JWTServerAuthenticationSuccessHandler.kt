package com.example.gateway.config.security.handler

import com.example.gateway.model.User
import com.example.gateway.service.TokenService
import com.example.starter.utils.exception.BadRequestException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик успешной аутентификации для JWT.
 * <p>
 * Этот обработчик предоставляет метод для обработки успешной аутентификации и формирования ответа с JWT-токенами.
 */
@Component
class JWTServerAuthenticationSuccessHandler(
    private val objectMapper: ObjectMapper,
    private val tokenService: TokenService
) : ServerAuthenticationSuccessHandler {

    /**
     * Обрабатывает успешную аутентификацию и формирует ответ с JWT-токенами.
     *
     * @param webFilterExchange объект WebFilterExchange, представляющий текущий обмен
     * @param authentication    объект Authentication, представляющий успешно аутентифицированного пользователя
     * @return Mono, представляющий завершение обработки и формирование ответа
     */
    @Transactional
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?
    ): Mono<Void> {
        if (authentication == null || webFilterExchange == null) {
            throw BadRequestException()
        }

        log.info { "Получение пользователя ${authentication.name} для аутентификации" }
        val principal = authentication.principal
        if (principal !is User) {
            log.info { "Ошибка получения пользователя" }
            return Mono.empty()
        }

        val response = webFilterExchange.exchange.response
        response.headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")

        return tokenService.getTokenPair(principal)
            .flatMap {
                log.debug { "Формирование ответа" }
                val dataBuffer: DataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(it))
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.writeWith(Mono.just(dataBuffer))
            }
    }
}
