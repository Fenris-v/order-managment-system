package com.example.gateway.dto.request.security

/**
 * DTO для запроса изменения данных пользователя.
 */
data class UserUpdatingRequest(val name: String? = null, val lastname: String? = null)
