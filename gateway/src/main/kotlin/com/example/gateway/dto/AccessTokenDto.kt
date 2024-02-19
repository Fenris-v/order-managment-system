package com.example.gateway.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * Класс представляет сущность токена доступа.
 */
data class AccessTokenDto(val id: UUID, val token: String, val userId: Long, val expireAt: LocalDateTime)
