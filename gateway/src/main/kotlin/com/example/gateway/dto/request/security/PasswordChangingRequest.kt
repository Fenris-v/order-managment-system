package com.example.gateway.dto.request.security

import jakarta.validation.constraints.NotBlank

data class PasswordChangingRequest(
    @field:NotBlank val password: String,
    @field:NotBlank val newPassword: String
)
