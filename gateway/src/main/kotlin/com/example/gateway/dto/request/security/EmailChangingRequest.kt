package com.example.gateway.dto.request.security

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailChangingRequest(@field:NotBlank @field:Email val email: String = "")
