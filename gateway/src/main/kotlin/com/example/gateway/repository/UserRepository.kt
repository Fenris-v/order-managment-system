package com.example.gateway.repository

import com.example.gateway.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveCrudRepository<User, Long> {
    fun findUserByEmail(email: String): Mono<User>

    fun existsByEmail(email: String): Mono<Boolean>
}
