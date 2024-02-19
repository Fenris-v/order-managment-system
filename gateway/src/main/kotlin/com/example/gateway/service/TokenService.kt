package com.example.gateway.service

import com.example.gateway.dto.AccessTokenDto
import com.example.gateway.dto.RefreshTokenDto
import com.example.gateway.model.AccessToken
import com.example.gateway.model.RefreshToken
import com.example.gateway.model.User
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Сервис для работы с токенами доступа и обновления.
 */
@Service
class TokenService(
    private val jwtUtil: JwtUtil,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    /**
     * Генерирует и сохраняет токен доступа для пользователя.
     *
     * @param accessId Уникальный идентификатор доступа
     * @param user     Пользователь
     * @return Моно с токеном доступа
     */
    fun getAccessToken(accessId: UUID, user: User): Mono<AccessTokenDto> {
        val token: String = jwtUtil.generateToken(user, accessId)
        val expiration: LocalDateTime = jwtUtil.extractExpiration(token)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val accessToken = AccessToken(accessId, user.id, expiration)
        val accessTokenDto = AccessTokenDto(accessId, token, user.id!!, expiration)

        return accessTokenRepository.save(accessToken).thenReturn(accessTokenDto)
    }

    /**
     * Генерирует и сохраняет токен обновления для пользователя.
     *
     * @param accessId Уникальный идентификатор доступа
     * @param user     Пользователь
     * @return Моно с токеном обновления
     */
    fun getRefreshToken(accessId: UUID, user: User): Mono<RefreshTokenDto> {
        val refreshId: UUID = UUID.randomUUID()
        val token: String = refreshTokenUtil.generateToken(user, refreshId)
        val expiration: LocalDateTime = refreshTokenUtil.extractExpiration(token)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val refreshToken = RefreshToken(refreshId, user.id, accessId, expiration)
        val refreshTokenDto = RefreshTokenDto(refreshId, token, user.id!!, accessId, expiration)

        return refreshTokenRepository.save(refreshToken).thenReturn(refreshTokenDto)
    }

    /**
     * Возвращает время жизни токена доступа.
     *
     * @return Время жизни токена доступа
     */
    fun getAccessExpiration(): Long {
        return jwtUtil.getExpiration().toSeconds()
    }
}
