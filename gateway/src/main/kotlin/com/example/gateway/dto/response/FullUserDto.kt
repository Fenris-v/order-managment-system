package com.example.gateway.dto.response

import java.time.LocalDateTime

/**
 * ДТО с данными пользователя.
 */
data class FullUserDto(
    val id: Long,
    val email: String,
    val roles: List<String>,
    val name: String?,
    val lastname: String?,
    val verifiedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
