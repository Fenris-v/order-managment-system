package com.example.gateway.service

import com.example.gateway.config.security.SecurityConfig
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.exception.ForbiddenException
import com.example.gateway.model.BlacklistToken
import com.example.gateway.model.RefreshToken
import com.example.gateway.model.User
import com.example.gateway.repository.BlacklistTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.repository.UserRepository
import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@Service
class UserDetailsService(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val blacklistTokenRepository: BlacklistTokenRepository
) : ReactiveUserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun findByUsername(username: String?): Mono<UserDetails> {
        return userRepository.findUserByEmail(username!!).cast(UserDetails::class.java)
    }

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

    @Transactional
    fun logout(exchange: ServerWebExchange): Mono<Void> {
        var token: String = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.empty()
        if (!token.startsWith("Bearer ")) {
            throw ForbiddenException()
        }

        try {
            token = token.substring(SecurityConfig.BEARER.length)
            val expireAt: Date = jwtUtil.extractExpiration(token)
            val blacklistToken = BlacklistToken(UUID.randomUUID(), token, expireAt)

            return blacklistTokenRepository.save(blacklistToken).then()
        } catch (ex: Exception) {
            logger.warn(ex.message)
            ex.printStackTrace()
            return Mono.empty()
        }
    }

    @Transactional
    fun refresh(refresh: String): Mono<JwtResponse> {
        return refreshTokenRepository.existsRefreshTokenByToken(refresh)
            .flatMap { if (it) Mono.error(ForbiddenException()) else saveRefreshToken(refresh) }
            .flatMap { generateTokens(refresh) }
    }

    private fun saveRefreshToken(refresh: String): Mono<RefreshToken> {
        val newRefreshToken = RefreshToken(UUID.randomUUID(), refresh, refreshTokenUtil.extractExpiration(refresh))
        return refreshTokenRepository.save(newRefreshToken)
    }

    private fun generateTokens(refresh: String): Mono<JwtResponse> {
        return findByUsername(refreshTokenUtil.extractUsername(refresh))
            .flatMap {
                val accessToken: String = jwtUtil.generateToken(it)
                val refreshToken: String = refreshTokenUtil.generateToken(it)
                Mono.just(JwtResponse(accessToken, refreshToken))
            }
    }
}
