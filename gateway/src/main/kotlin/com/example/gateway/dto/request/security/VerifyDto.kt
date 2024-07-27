package com.example.gateway.dto.request.security

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * DTO для запроса повторного письма для верификации почты.
 */
data class VerifyDto(@field:NotBlank @field:Email val email: String = "")
