package com.example.payment.repository

import com.example.payment.enums.UPaymentStatus
import com.example.payment.model.Transaction
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Интерфейс репозитория для работы с данными о платежах.
 */
interface TransactionRepository : ReactiveCrudRepository<Transaction, Long> {

    /**
     * Обновляет статус платежа по его идентификатору.
     *
     * @param status статус платежа.
     * @param paymentId идентификатор платежа.
     */
    @Query(value = "UPDATE transactions SET status = :status WHERE payment_id = :paymentId")
    fun updateStatusByPaymentId(status: String, paymentId: UUID): Mono<Unit>

    /**
     * Находит платеж по его идентификатору.
     *
     * @param paymentId идентификатор платежа.
     */
    fun findTransactionByPaymentId(paymentId: UUID): Mono<Transaction>

    /**
     * Находит все платежи по статусу.
     *
     * @param status статус платежа.
     */
    fun findTransactionsByStatus(status: UPaymentStatus): Flux<Transaction>

    /**
     * Находит платежи пользователя.
     *
     * @param userId идентификатор пользователя.
     * @param size количество платежей.
     * @param offset смещение.
     */
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY created_at DESC LIMIT :size OFFSET :offset")
    fun getTransactionHistory(userId: Long, size: Int, offset: Int): Flux<Transaction>
}
