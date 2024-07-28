package com.example.gateway.util.jwt

import com.example.gateway.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Утилита для работы с JWT-токенами.
 */
@Component
class RefreshTokenUtil(
    @Value("\${app.auth.refresh.secret}") private val secret: String,
    @Value("\${app.auth.refresh.expiration}") private val expiration: Duration
) : AbstractTokenUtil(), TokenUtil {
    override fun getExpiration(): Duration {
        return expiration
    }

    override fun getSecret(): String {
        return secret
    }

    override fun getClaims(user: User): Map<String, Any> {
        return HashMap()
    }
}
