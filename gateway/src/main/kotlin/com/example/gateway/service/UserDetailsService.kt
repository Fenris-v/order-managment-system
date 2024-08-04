package com.example.gateway.service

import com.example.gateway.config.security.SecurityConfig.Companion.BEARER
import com.example.gateway.dto.NinjaPasswordResponse
import com.example.gateway.dto.VerifyToken
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.request.security.EmailRequest
import com.example.gateway.dto.request.security.PasswordChangingRequest
import com.example.gateway.dto.request.security.UserUpdatingRequest
import com.example.gateway.dto.response.FullUserDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.event.UserRegisteredEvent
import com.example.gateway.exception.EntityNotFoundException
import com.example.gateway.exception.ForbiddenException
import com.example.gateway.exception.UnauthorizedException
import com.example.gateway.exception.UnprocessableException
import com.example.gateway.mapper.UserMapper
import com.example.gateway.model.User
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.repository.RefreshTokenRepository
import com.example.gateway.repository.UserRepository
import com.example.gateway.service.mail.MailService
import com.example.gateway.util.jwt.JwtUtil
import com.example.gateway.util.jwt.RefreshTokenUtil
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.Base64

private val log: KLogger = KotlinLogging.logger {}

/**
 * Сервис для работы с данными пользователей в системе.
 */
@Service
class UserDetailsService(
    private val jwtUtil: JwtUtil,
    private val userMapper: UserMapper,
    private val mailService: MailService,
    private val tokenService: TokenService,
    private val verifyService: VerifyService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val passwordService: NinjasPasswordService,
    private val eventPublisher: ApplicationEventPublisher,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : ReactiveUserDetailsService {
    /**
     * Получение пользователя по id.
     *
     * @return Моно с UserDto.
     */
    @Transactional(readOnly = true)
    fun getCurrentUserResponse(): Mono<FullUserDto> {
        return getCurrentUser().map { userMapper.mapEntityToFullUserDto(it) }
    }

    /**
     * Получение пользователя по id.
     *
     * @param userId Идентификатор пользователя.
     * @return Моно с UserDto.
     */
    @Transactional(readOnly = true)
    fun getUserById(userId: Long): Mono<UserDto> {
        return userRepository.findById(userId).switchIfEmpty(Mono.error(EntityNotFoundException()))
            .map { userMapper.mapEntityToDto(it) }
    }

    /**
     * Обновление пользователя.
     *
     * @param request Данные для обновления.
     * @return Моно с UserDto.
     */
    @Transactional
    fun updateUser(request: UserUpdatingRequest): Mono<FullUserDto> {
        return getCurrentUser().flatMap {
            when {
                it !is User -> Mono.error(UnauthorizedException())
                else -> {
                    it.name = request.name
                    it.lastname = request.lastname
                    it.updatedAt = LocalDateTime.now()
                    userRepository.save(it)
                }
            }
        }.map { userMapper.mapEntityToFullUserDto(it) }
    }

    /**
     * Изменить пароль.
     *
     * @param request Данные для обновления.
     * @return Моно без результата.
     */
    @Transactional
    fun changePassword(authorization: String, request: PasswordChangingRequest): Mono<Void> {
        return getCurrentUser().flatMap {
            when {
                !passwordEncoder.matches(
                    request.password,
                    it.password
                ) -> Mono.error(UnprocessableException("Неправильный пароль"))

                else -> {
                    it.password = passwordEncoder.encode(request.newPassword)
                    it.updatedAt = LocalDateTime.now()
                    userRepository.save(it)
                }
            }
        }.flatMap {
            val token: String = authorization.substring(BEARER.length)
            accessTokenRepository.deleteWhereIdNot(jwtUtil.extractTokenId(token))
        }.then()
    }

    fun resetPassword(request: EmailRequest): Mono<Void> {
        return userRepository.findUserByEmailIgnoreCase(request.email)
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .flatMap { user ->
                passwordService.generatePassword()
                    .flatMap { ninjaPassword ->
                        user.password = passwordEncoder.encode(ninjaPassword.password)
                        user.updatedAt = LocalDateTime.now()

                        userRepository.save(user)
                            .flatMap { Mono.just(mailService.sendPasswordResetMail(user, ninjaPassword.password)) }
                    }
            }.then()
    }

    fun generatePassword(length: Int? = null): Mono<NinjaPasswordResponse> {
        return passwordService.generatePassword(length)
    }

    /**
     * Запрос на изменение электронной почты.
     *
     * @param request Данные для обновления.
     * @return Моно без результата.
     */
    @Transactional
    fun changeEmail(request: EmailRequest): Mono<Void> {
        return getCurrentUser().flatMap { verifyService.sendChangeEmailLetter(it, request.email) }.then()
    }

    /**
     * Подтвердит смену электронной почты.
     *
     * @param token Токен подтверждения.
     * @return Моно с JwtResponse, содержащим новые токены.
     */
    @Transactional
    fun verifyChangeEmail(token: String): Mono<JwtResponse> {
        val decodedString = String(Base64.getDecoder().decode(token))
        val verifyToken: VerifyToken = Json.decodeFromString<VerifyToken>(decodedString)
        if (VerifyToken.getExpiresDateTime(verifyToken).isBefore(LocalDateTime.now())) {
            return Mono.error(UnauthorizedException())
        }

        return getCurrentUser().flatMap {
            when {
                it.email != verifyToken.email -> Mono.error(ForbiddenException())
                it.confirmationToken != token -> Mono.error(ForbiddenException())
                verifyToken.newEmail == null -> Mono.error(UnprocessableException("Новая почта не может быть пустой"))
                else -> {
                    it.verifiedAt = LocalDateTime.now()
                    it.confirmationToken = null
                    it.email = verifyToken.newEmail

                    userRepository.save(it).thenReturn(it)
                }
            }
        }.flatMap {
            accessTokenRepository.deleteAllByUserId(it.id!!).then(tokenService.getTokenPair(it))
        }
    }

    /**
     * Поиск пользователя по электронной почте.
     *
     * @param username Электронная почта.
     * @return Моно с UserDetails.
     */
    @Transactional(readOnly = true)
    override fun findByUsername(username: String?): Mono<UserDetails> {
        return userRepository.findUserByEmailIgnoreCase(username!!).onErrorResume { ex ->
            log.error(ex) { "Ошибка аутентификации - $username\n${ex.message}" }
            return@onErrorResume Mono.empty()
        }.cast(UserDetails::class.java)
    }

    /**
     * Создание нового пользователя.
     *
     * @param authDto Регистрационные данные.
     * @return Моно с UserDto.
     */
    @Transactional
    fun createUser(authDto: AuthDto): Mono<JwtResponse> {
        val user = User(authDto.username, passwordEncoder.encode(authDto.password))
        user.confirmationToken = verifyService.getVerifyTokenString(user.email)

        return userRepository.save(user).flatMap {
            userRepository.findUserByEmailIgnoreCase(it.email).flatMap { savedUser ->
                eventPublisher.publishEvent(UserRegisteredEvent(this, savedUser))
                tokenService.getTokenPair(savedUser)
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
    fun refresh(token: String): Mono<JwtResponse> {
        return Mono.just(!refreshTokenUtil.isValidToken(token)).onErrorResume { Mono.error(UnauthorizedException()) }
            .flatMap {
                refreshTokenRepository.findAccessIdByTokenId(refreshTokenUtil.extractTokenId(token))
                    .switchIfEmpty(Mono.error(UnauthorizedException()))
                    .flatMap { accessId -> accessTokenRepository.deleteById(accessId) }
                    .then(tokenService.getTokenPair(token))
            }
    }

    private fun getCurrentUser(): Mono<User> {
        return ReactiveSecurityContextHolder.getContext().map { it.authentication }.map { it.principal }
            .cast(String::class.java).flatMap { userRepository.findUserByEmailIgnoreCase(it) }
    }
}
