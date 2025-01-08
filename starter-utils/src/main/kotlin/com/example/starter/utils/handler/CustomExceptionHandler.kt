package com.example.starter.utils.handler

import com.example.starter.utils.dto.response.ExceptionDto
import com.example.starter.utils.dto.response.ValidatorResponse
import com.example.starter.utils.exception.EntityNotFoundException
import com.example.starter.utils.exception.ForbiddenException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

private val log: KLogger = KotlinLogging.logger {}

/**
 * Обработчик исключений для контроллеров. Предоставляет обработку различных видов исключений и возвращает
 * соответствующие ответы с информацией об ошибке.
 */
abstract class AbstractExceptionHandler {
    /**
     * Обрабатывает исключение ForbiddenException и возвращает ответ с кодом 403 и информацией об ошибке.
     *
     * @param ex Исключение при ошибке доступа.
     * @return Ответ с информацией об ошибке и кодом 403.
     */
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<Any> {
        log.error(ex) { ex.message }
        val status = HttpStatus.FORBIDDEN
        val dto = ExceptionDto("Forbidden", status.value())
        return ResponseEntity(dto, status)
    }

    /**
     * Обрабатывает исключение ServerWebInputException и возвращает ответ с кодом 400 и информацией об ошибке.
     *
     * @param ex Исключение при ошибках ввода.
     * @return Ответ с информацией об ошибке и кодом 400.
     */
    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ExceptionDto> {
        log.error(ex) { ex.message }
        val status = HttpStatus.BAD_REQUEST
        val dto = ExceptionDto(ex.message, status.value())
        return ResponseEntity<ExceptionDto>(dto, status)
    }

    /**
     * Обрабатывает исключение EntityNotFoundException и возвращает ответ с кодом 404 и информацией об ошибке.
     *
     * @param ex Исключение при отсутствии сущности с заданными параметрами.
     * @return Ответ с информацией об ошибке и кодом 404.
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Any> {
        log.error(ex) { ex.message }
        val status = HttpStatus.NOT_FOUND
        val dto = ExceptionDto("Not found", status.value())
        return ResponseEntity(dto, status)
    }

    /**
     * Обрабатывает исключение WebExchangeBindException и возвращает ответ с кодом 422 и информацией об ошибках
     * валидации.
     *
     * @param ex Исключение WebExchangeBindException.
     * @return Ответ с информацией об ошибках валидации и кодом 422.
     */
    @ExceptionHandler(WebExchangeBindException::class)
    protected fun handleMethodArgumentNotValid(ex: WebExchangeBindException): ResponseEntity<ValidatorResponse> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.allErrors.forEach { addError(errors, it) }

        return ResponseEntity(ValidatorResponse(errors), HttpStatus.UNPROCESSABLE_ENTITY)
    }

    private fun addError(errors: MutableMap<String, String>, error: ObjectError) {
        val fieldName: String = (error as FieldError).field
        val message: String = error.getDefaultMessage() ?: ""
        errors[fieldName] = message
    }
}
