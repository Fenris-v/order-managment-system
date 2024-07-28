package com.example.gateway.dto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * DTO для создания токена верификации.
 */
@Serializable
data class VerifyToken(val email: String, val expiresAt: String, val newEmail: String? = null) {
    companion object {
        private val formatter = DateTimeFormatter.ISO_DATE_TIME

        /**
         * Создает новый объект для токена верификации.
         *
         * @param email Электронная почта.
         * @param expiresAt Время истечения токена.
         * @return Новый объект для токена верификации.
         */
        fun create(email: String, expiresAt: LocalDateTime): VerifyToken {
            return VerifyToken(email, expiresAt.format(formatter))
        }

        /**
         * Создает новый объект для токена верификации.
         *
         * @param email Электронная почта.
         * @param expiresAt Время истечения токена.
         * @param newEmail Новая электронная почта.
         * @return Новый объект для токена верификации.
         */
        fun create(email: String, expiresAt: LocalDateTime, newEmail: String?): VerifyToken {
            return VerifyToken(email, expiresAt.format(formatter), newEmail)
        }

        /**
         * Получить время истечения токена.
         *
         * @param verifyToken Токен верификации.
         * @return Время истечения токена.
         */
        fun getExpiresDateTime(verifyToken: VerifyToken): LocalDateTime {
            return LocalDateTime.parse(verifyToken.expiresAt, formatter)
        }
    }
}
