package com.example.gateway.repository

import com.example.gateway.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

/**
 * Репозиторий для взаимодействия с таблицей пользователей в базе данных.
 */
interface UserRepository : ReactiveCrudRepository<User, Long> {

    /**
     * Ищет пользователя по электронной почте (без учета регистра).
     *
     * @param email Электронная почта пользователя.
     * @return Mono, возвращающий найденного пользователя или null, если пользователь не найден.
     */
    fun findUserByEmailIgnoreCase(email: String): Mono<User>

    /**
     * Ищет пользователя по email.
     *
     * @param email Email пользователя.
     * @return Mono, возвращающий найденного пользователя или null, если пользователь не найден.
     */
    fun existsByEmailIgnoreCase(email: String): Mono<Boolean>
}
