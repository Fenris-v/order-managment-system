package com.example.gateway.dto.response

import java.time.LocalDateTime

/**
 * Класс, представляющий объект ответа на успешную аутентификацию с токенами.
 */
data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String = "Bearer",
    val timestamp: LocalDateTime = LocalDateTime.now()
)
