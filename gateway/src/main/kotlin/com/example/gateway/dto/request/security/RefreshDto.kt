package com.example.gateway.dto.request.security

/**
 * Класс, представляющий объект запроса для обновления токена.
 */
data class RefreshDto(val refreshToken: String = "")
