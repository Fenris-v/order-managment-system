package com.example.gateway.service

import com.example.gateway.dto.AccessTokenDto
import com.example.gateway.dto.RefreshTokenDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.model.AccessToken
import com.example.gateway.model.RefreshToken
import com.example.gateway.model.User
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.repository.UserRepository
import com.example.gateway.util.jwt.JwtUtil
import com.example.gateway.util.jwt.RefreshTokenUtil
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

private val log: KLogger = KotlinLogging.logger {}

/**
 * Сервис для работы с токенами доступа и обновления.
 */
@Service
class TokenService(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    /**
     * Генерирует пару токенов доступа и обновления для пользователя.
     *
     * @param user Сущность пользователя
     * @return Моно с токенами доступа и обновления
     */
    @Transactional
    fun getTokenPair(user: User): Mono<JwtResponse> {
        log.debug { "Выпуск авторизационных токенов" }
        val accessId = UUID.randomUUID()
        return getAccessToken(accessId, user)
            .zipWith(getRefreshToken(accessId, user))
            .map { JwtResponse(it.t1.token, it.t2.token, getAccessExpiration()) }
    }

    /**
     * Генерирует пару токенов доступа и обновления для пользователя на основании токена обновления.
     *
     * @param token Токен обновления
     * @return Моно с токенами доступа и обновления
     */
    @Transactional
    fun getTokenPair(token: String): Mono<JwtResponse> {
        val accessId = UUID.randomUUID()
        return userRepository.findUserByEmailIgnoreCase(refreshTokenUtil.extractUsername(token))
            .flatMap {
                getAccessToken(accessId, it)
                    .zipWith(getRefreshToken(accessId, it))
                    .map { tuple -> JwtResponse(tuple.t1.token, tuple.t2.token, getAccessExpiration()) }
            }
    }

    /**
     * Генерирует и сохраняет токен доступа для пользователя.
     *
     * @param accessId Уникальный идентификатор доступа
     * @param user     Пользователь
     * @return Моно с токеном доступа
     */
    private fun getAccessToken(accessId: UUID, user: User): Mono<AccessTokenDto> {
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
    private fun getRefreshToken(accessId: UUID, user: User): Mono<RefreshTokenDto> {
        val refreshId: UUID = UUID.randomUUID()
        val token: String = refreshTokenUtil.generateToken(user, refreshId)
        val expiration: LocalDateTime = refreshTokenUtil.extractExpiration(token)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val refreshToken = RefreshToken(refreshId, accessId, expiration)
        val refreshTokenDto = RefreshTokenDto(refreshId, token, accessId, expiration)

        return refreshTokenRepository.save(refreshToken).thenReturn(refreshTokenDto)
    }

    private fun getAccessExpiration(): Long {
        return jwtUtil.getExpiration().toSeconds()
    }
}
