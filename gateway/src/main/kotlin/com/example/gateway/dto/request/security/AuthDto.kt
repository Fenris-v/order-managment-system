package com.example.gateway.dto.request.security

import com.example.gateway.annotation.validation.UserNotExists
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * Запись, представляющая объект данных для аутентификации (DTO).
 */
data class AuthDto(
    @field:Email
    @field:NotNull
    @field:UserNotExists
    @field:Size(min = 2, max = 255)
    val username: String,

    @field:NotNull
    @field:Size(min = 8, max = 255)
    val password: String
)
