package com.example.gateway.dto.response

import java.time.LocalDateTime

/**
 * Класс, который служит для отправки ответа при возникновении исключений
 */
data class ExceptionDto(val message: String, val status: Int, val timestamp: LocalDateTime = LocalDateTime.now())
