package com.example.gateway.service

import com.example.gateway.dto.NinjaPasswordResponse
import com.example.gateway.util.HttpUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Сервис-интеграция для генерации пароля.
 */
@Service
class NinjasPasswordService(
    @Value("\${app.ninjas.api-key}") private val apiKey: String,
    @Value("\${app.ninjas.base-url}") private val baseUrl: String,
    private val httpUtil: HttpUtil
) {

    companion object {
        private const val PASSWORD_LENGTH = 16
        private const val GENERATOR_API_URL = "%s%s?length=%d"
        private const val PASSWORD_GENERATOR_URI = "passwordgenerator"
    }

    /**
     * Генерация пароля.
     *
     * @param length Длина пароля
     * @return Пароль
     */
    fun generatePassword(length: Int? = null): Mono<NinjaPasswordResponse> {
        return httpUtil.get(
            String.format(GENERATOR_API_URL, baseUrl, PASSWORD_GENERATOR_URI, length ?: PASSWORD_LENGTH),
            apiKey,
            NinjaPasswordResponse::class.java
        )
    }
}
