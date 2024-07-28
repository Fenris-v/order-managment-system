package com.example.gateway.dto.response

import java.time.LocalDateTime

/**
 * Класс, представляющий объект ответа на эндпоинты с пустыми ответами.
 */
data class EmptyResponse(val message: String, val timestamp: LocalDateTime = LocalDateTime.now())
