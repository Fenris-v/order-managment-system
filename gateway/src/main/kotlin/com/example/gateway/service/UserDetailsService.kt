package com.example.gateway.service

import com.example.gateway.config.security.SecurityConfig.Companion.BEARER
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.exception.BadRequestException
import com.example.gateway.exception.UnauthorizedException
import com.example.gateway.model.User
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.repository.UserRepository
import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

private val log: KLogger = KotlinLogging.logger {}

/**
 * Сервис для работы с данными пользователей в системе.
 */
@Service
class UserDetailsService(
    private val jwtUtil: JwtUtil,
    private val tokenService: TokenService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : ReactiveUserDetailsService {
    /**
     * Поиск пользователя по электронной почте.
     *
     * @param username Электронная почта.
     * @return Моно с UserDetails.
     */
    override fun findByUsername(username: String?): Mono<UserDetails> {
        return userRepository.findUserByEmailIgnoreCase(username!!)
            .onErrorResume { ex ->
                log.error(ex) { String.format("Ошибка аутентификации - %s\n%s", username, ex.message) }
                return@onErrorResume Mono.empty();
            }.cast(UserDetails::class.java)
    }

    /**
     * Создание нового пользователя.
     *
     * @param authDto Регистрационные данные.
     * @return Моно с UserDto.
     */
    @Transactional
    fun createUser(authDto: AuthDto): Mono<UserDto> {
        val user = User(authDto.username, passwordEncoder.encode(authDto.password))
        // todo: изменить логику подтверждения
        user.verifiedAt = LocalDateTime.now()

        return userRepository.save(user).flatMap {
            findByUsername(user.username).cast(User::class.java).flatMap { user ->
                val roles: MutableList<String> = ArrayList()
                user.authorities.forEach { roles.add(it.authority) }

                Mono.just(UserDto(user.id, user.username, roles, it.name, it.lastname, it.verifiedAt))
            }
        }
    }

    /**
     * Выход пользователя из системы (удаление токена доступа).
     *
     * @param authorization Заголовок авторизации с токеном доступа
     * @return Моно без результата
     */
    @Transactional
    fun logout(authorization: String): Mono<Void> {
        val token: String = authorization.substring(BEARER.length)
        return accessTokenRepository.deleteById(jwtUtil.extractTokenId(token))
    }

    /**
     * Обновление токена доступа с использованием токена обновления.
     *
     * @param token Токен обновления
     * @return Моно с JwtResponse
     */
    @Transactional
    fun refresh(token: String): Mono<JwtResponse> {
        try {
            if (!refreshTokenUtil.isValidToken(token)) {
                throw UnauthorizedException()
            }
        } catch (ex: JwtException) {
            throw UnauthorizedException()
        }

        return refreshTokenRepository
            .findAccessIdByTokenId(refreshTokenUtil.extractTokenId(token))
            .switchIfEmpty(Mono.error(UnauthorizedException()))
            .flatMap { accessId -> accessTokenRepository.deleteById(accessId) }
            .then(generateTokens(token))
    }

    private fun generateTokens(token: String): Mono<JwtResponse> {
        val accessId = UUID.randomUUID()
        return findByUsername(refreshTokenUtil.extractUsername(token))
            .flatMap { user ->
                if (user !is User) {
                    return@flatMap Mono.error<JwtResponse>(BadRequestException())
                }

                tokenService
                    .getAccessToken(accessId, user)
                    .zipWith(tokenService.getRefreshToken(accessId, user))
                    .map { tuple ->
                        JwtResponse(
                            tuple.t1.token,
                            tuple.t2.token,
                            tokenService.getAccessExpiration()
                        )
                    }
            }
    }
}
