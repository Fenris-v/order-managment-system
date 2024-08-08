package com.example.payment.service

import com.example.payment.dto.response.UserBalanceResponse
import com.example.payment.repository.UserBalanceRepository
import com.example.starter.utils.utils.jwt.JwtUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * Сервис для работы с балансом пользователя.
 */
@Service
class UserBalanceService(
    private val userBalanceRepository: UserBalanceRepository,
    private val jwtUtils: JwtUtils
) {
    /**
     * Добавляет деньги на баланс пользователя.
     *
     * @param userId идентификатор пользователя
     * @param amount сумма денег
     *
     * @return Mono<Void>
     */
    @Transactional
    fun topUpBalance(userId: Long, amount: Double): Mono<Void> {
        return userBalanceRepository.topUpBalance(userId, amount)
    }

    /**
     * Запрашивает баланс пользователя.
     *
     * @param authorization токен пользователя
     *
     * @return Mono<UserBalanceResponse>
     */
    fun getBalance(authorization: String): Mono<UserBalanceResponse> {
        return Mono.just(jwtUtils.extractAllClaims(authorization))
            .flatMap { user ->
                userBalanceRepository.findUserBalanceByUserId(user.id)
                    .map { UserBalanceResponse(it.amount ?: 0.0, it.updatedAt) }
                    .switchIfEmpty(Mono.just(UserBalanceResponse(0.0, LocalDateTime.now())))
            }
    }
}
