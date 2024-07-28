package com.example.gateway.dto.response

/**
 * ДТО с данными пользователя
 */
data class UserDto(
    val id: Long,
    val email: String,
    val name: String?,
    val lastname: String?
)
