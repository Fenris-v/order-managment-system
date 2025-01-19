package com.example.gateway.controller

import com.example.gateway.exception.ThrottleException
import com.example.starter.utils.dto.response.ExceptionDto
import com.example.starter.utils.handler.AbstractExceptionHandler
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val log: KLogger = KotlinLogging.logger {}

/**
 * Глобальный обработчик исключений для контроллеров. Предоставляет обработку различных видов исключений и возвращает
 * соответствующие ответы с информацией об ошибке.
 */
@ControllerAdvice
class CustomExceptionHandler : AbstractExceptionHandler() {

    /**
     * Обрабатывает исключение ThrottleException и возвращает ответ с кодом 403 и информацией об ошибке.
     *
     * @param ex Исключение при ошибке доступа.
     * @return Ответ с информацией об ошибке и кодом 403.
     */
    @ExceptionHandler(ThrottleException::class)
    fun handleThrottleException(ex: ThrottleException): ResponseEntity<Any> {
        log.error(ex) { ex.message }
        val status = HttpStatus.FORBIDDEN
        val dto = ExceptionDto(ex.message!!, status.value())
        return ResponseEntity(dto, status)
    }
}
