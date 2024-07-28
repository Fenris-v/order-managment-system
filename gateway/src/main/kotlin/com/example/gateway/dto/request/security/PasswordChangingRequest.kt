package com.example.gateway.dto.request.security

import jakarta.validation.constraints.NotBlank

/**
 * Запись, представляющая объект данных для изменения пароля.
 */
data class PasswordChangingRequest(
    @field:NotBlank val password: String,
    @field:NotBlank val newPassword: String
)
