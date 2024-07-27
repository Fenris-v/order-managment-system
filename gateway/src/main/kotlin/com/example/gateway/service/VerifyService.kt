package com.example.gateway.service

import com.example.gateway.dto.VerifyToken
import com.example.gateway.dto.request.security.VerifyDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.exception.BadRequestException
import com.example.gateway.exception.ForbiddenException
import com.example.gateway.exception.ThrottleException
import com.example.gateway.exception.UnauthorizedException
import com.example.gateway.model.User
import com.example.gateway.repository.UserRepository
import com.example.gateway.service.mail.MailService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.Base64

@Service
class VerifyService(private val mailService: MailService, private val userRepository: UserRepository) {
    companion object {
        private const val TOKEN_TTL = 60L
    }

    /**
     * Создаёт и возвращает сериализованный в Base64 строку VerifyToken.
     */
    fun getVerifyTokenString(email: String, newEmail: String? = null): String {
        val verifyToken = VerifyToken.create(email, LocalDateTime.now().plusMinutes(TOKEN_TTL), newEmail)
        return Base64.getEncoder().encodeToString(Json.encodeToString(verifyToken).toByteArray())
    }

    /**
     * Подтверждение email пользователя.
     *
     * @param token Токен подтверждения.
     * @return Моно без результата
     */
    @Transactional
    fun verify(token: String): Mono<Void> {
        val decodedString = String(Base64.getDecoder().decode(token))
        val verifyToken: VerifyToken = Json.decodeFromString<VerifyToken>(decodedString)
        if (VerifyToken.getExpiresDateTime(verifyToken).isBefore(LocalDateTime.now())) {
            return Mono.error(UnauthorizedException())
        }

        return userRepository.findUserByEmailIgnoreCase(verifyToken.email)
            .flatMap {
                when {
                    it !is User -> Mono.error(UnauthorizedException())
                    it.confirmationToken != token -> Mono.error(UnauthorizedException())
                    else -> {
                        it.verifiedAt = LocalDateTime.now()
                        it.confirmationToken = null
                        userRepository.save(it).then(Mono.empty())
                    }
                }
            }
    }

    /**
     * Отправка письма для подтверждения смены email пользователя.
     *
     * @param user Пользователь.
     * @param newEmail Новая электронная почта.
     * @return Моно без результата.
     */
    @Transactional
    fun sendChangeEmailLetter(user: User, newEmail: String): Mono<Void> {
        user.confirmationToken = getVerifyTokenString(user.email, newEmail)
        return userRepository.save(user)
            .then(Mono.fromRunnable { mailService.sendChangeEmailLetter(user, newEmail) })
    }

    /**
     * Отправка письма для подтверждения email пользователя.
     *
     * @param verifyDto Данные для подтверждения.
     * @return Моно без результата
     */
    @Transactional
    fun sendVerifyEmail(verifyDto: VerifyDto): Mono<Void> {
        return userRepository.findUserByEmailIgnoreCase(verifyDto.email)
            .flatMap {
                when {
                    it !is User -> Mono.error<JwtResponse>(BadRequestException())
                    it.verifiedAt != null -> Mono.error<Void>(ForbiddenException())
                    it.confirmationToken != null -> handleWhenConfirmationTokenExist(it)
                    else -> sendVerifyEmail(it)
                }
            }
            .then()
    }

    private fun handleWhenConfirmationTokenExist(user: User): Mono<Void> {
        val decodedString = String(Base64.getDecoder().decode(user.confirmationToken))
        val verifyToken: VerifyToken = Json.decodeFromString(decodedString)
        val isThrottlePass: Boolean = VerifyToken.getExpiresDateTime(verifyToken)
            .isBefore(LocalDateTime.now().plusMinutes(TOKEN_TTL - 1))

        return if (isThrottlePass) sendVerifyEmail(user) else Mono.error(ThrottleException())
    }

    private fun sendVerifyEmail(user: User): Mono<Void> {
        user.confirmationToken = getVerifyTokenString(user.email)
        return userRepository.save(user)
            .then(Mono.fromRunnable { mailService.sendVerifyMail(user) })
    }
}
