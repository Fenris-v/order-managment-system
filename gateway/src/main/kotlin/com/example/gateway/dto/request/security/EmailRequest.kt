package com.example.gateway.dto.request.security

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Запись, представляющая объект данных для обновления электронной почты.
 */
data class EmailRequest(@field:NotBlank @field:Email val email: String = "")
