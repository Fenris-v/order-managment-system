package com.example.gateway.controller

import com.example.gateway.dto.response.ExceptionDto
import com.example.gateway.dto.response.ValidatorResponse
import com.example.gateway.exception.EntityNotFoundException
import com.example.gateway.exception.ForbiddenException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(): ResponseEntity<Any> {
        val status = HttpStatus.FORBIDDEN
        val dto = ExceptionDto("Forbidden", status.value())
        return ResponseEntity(dto, status)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ExceptionDto> {
        val status = HttpStatus.BAD_REQUEST
        val dto = ExceptionDto(ex.message, status.value())
        return ResponseEntity<ExceptionDto>(dto, status)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(): ResponseEntity<Any> {
        val status = HttpStatus.NOT_FOUND
        val dto = ExceptionDto("Not found", status.value())
        return ResponseEntity(dto, status)
    }

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
