package com.example.gateway.util.jwt

import com.example.gateway.model.User
import java.util.UUID

/**
 * Интерфейс для утилиты работы с токенами.
 */
interface TokenUtil {
    /**
     * Генерация токена для конкретного пользователя.
     *
     * @param user Пользователь
     * @param id   Идентификатор токена
     * @return Строка со сгенерированным токеном
     */
    fun generateToken(user: User, id: UUID): String
}
