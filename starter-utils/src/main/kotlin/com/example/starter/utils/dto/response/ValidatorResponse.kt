package com.example.starter.utils.dto.response

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

/**
 * Класс данных (DTO) для представления ответа при ошибках валидации данных.
 */
data class ValidatorResponse(
    val messages: MutableMap<String, String>,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int = HttpStatus.UNPROCESSABLE_ENTITY.value()
)
