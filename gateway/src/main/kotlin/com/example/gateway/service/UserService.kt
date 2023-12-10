package com.example.gateway.service

import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.exception.EntityNotFoundException
import com.example.gateway.model.RefreshToken
import com.example.gateway.model.User
import com.example.gateway.repository.BlacklistTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.repository.UserRepository
import com.example.gateway.util.JwtUtil
import com.example.gateway.util.RefreshTokenUtil
import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    private val blacklistTokenRepository: BlacklistTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val refreshUtil: RefreshTokenUtil,
    private val jwtUtil: JwtUtil,
) : ReactiveUserDetailsService {
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

    // TODO: проверить транзакционность с реактивностью
    @Transactional
    fun authenticateUser(authDto: AuthDto): Mono<JwtResponse> {
        return findByUsername(authDto.username)
            .cast(User::class.java)
            .filter { passwordEncoder.matches(authDto.password, it.password) }
            .flatMap { user ->
                makeRefreshToken(user).flatMap { refresh ->
                    jwtUtil.generateToken(user).map {
                        JwtResponse(it, refresh, LocalDateTime.now())
                    }
                }
            }
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
    }

    private fun makeRefreshToken(user: User): Mono<String> {
        return refreshUtil.generateToken(user).flatMap { token ->
            refreshUtil.extractExpiration(token).flatMap {
                val refreshToken = RefreshToken(UUID.randomUUID(), token, it)
                refreshTokenRepository.save(refreshToken).thenReturn(token)
            }
        }
    }

    override fun findByUsername(username: String?): Mono<UserDetails> {
        if (username == null) {
            throw EntityNotFoundException()
        }

        return userRepository.findUserByEmail(username).cast(UserDetails::class.java)
    }
}
