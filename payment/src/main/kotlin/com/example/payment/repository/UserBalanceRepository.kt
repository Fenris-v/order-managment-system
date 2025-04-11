package com.example.payment.repository

import com.example.payment.model.UserBalance
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

/**
 * Интерфейс репозитория для работы с данными о балансе пользователя.
 */
interface UserBalanceRepository : ReactiveCrudRepository<UserBalance, Long> {

    /**
     * Добавляет деньги на баланс пользователя.
     */
    @Query(
        """
        INSERT INTO user_balances (user_id, amount) VALUES (:userId, :amount)
        ON CONFLICT (user_id) DO UPDATE SET amount = user_balances.amount + :amount, updated_at = CURRENT_TIMESTAMP
        """
    )
    fun topUpBalance(userId: Long, amount: Double): Mono<Unit>

    /**
     * Находит баланс пользователя по его идентификатору.
     */
    fun findUserBalanceByUserId(userId: Long): Mono<UserBalance>
}
