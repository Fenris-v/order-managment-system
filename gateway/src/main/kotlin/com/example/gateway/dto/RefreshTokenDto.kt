package com.example.gateway.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * Класс представляет ДТО токена обновления.
 */
data class RefreshTokenDto(
    val id: UUID,
    val token: String,
    val userId: Long,
    val accessId: UUID,
    val expireAt: LocalDateTime
)
