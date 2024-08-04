package com.example.starter.utils.config

import com.example.starter.utils.utils.jwt.ClaimsUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Класс автоматической конфигурации стартера. Регистрирует бины для работы с JWT.
 */
@Configuration
@ConditionalOnProperty(name = ["starter.utils.jwt"], havingValue = "true")
class JwtAutoConfiguration {
    /**
     * Регистрирует бин ClaimsUtils.
     *
     * @param objectMapper Экземпляр ObjectMapper
     * @return Бин ClaimsUtils
     */
    @Bean
    fun claimsUtils(objectMapper: ObjectMapper): ClaimsUtils {
        return ClaimsUtils(objectMapper)
    }
}
