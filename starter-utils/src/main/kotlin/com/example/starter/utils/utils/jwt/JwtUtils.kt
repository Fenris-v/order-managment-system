package com.example.starter.utils.utils.jwt

import com.example.starter.utils.dto.JwtUserDto
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.Base64

/**
 * Класс-утилита для работы с JWT.
 */
class JwtUtils(private val objectMapper: ObjectMapper) {
    companion object {
        const val BEARER: String = "Bearer "
    }

    /**
     * Обрезает Bearer из токена.
     *
     * @param token JWT токен
     * @return Токен без Bearer
     */
    fun cutBearer(token: String): String {
        return token.substring(BEARER.length)
    }

    /**
     * Извлечение полезной нагрузки из токена.
     *
     * @param token JWT токен
     * @return Полезная нагрузка
     */
    fun extractAllClaims(token: String): JwtUserDto {
        val payload = String(Base64.getDecoder().decode(cutBearer(token).split('.')[1]))
        return objectMapper.readValue(payload, PayloadData::class.java).user
    }
}

/**
 * Класс для отображения полезной нагрузки.
 */
private data class PayloadData(@JsonProperty("user") val user: JwtUserDto)
