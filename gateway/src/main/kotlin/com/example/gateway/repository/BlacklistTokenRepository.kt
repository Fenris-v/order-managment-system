package com.example.gateway.repository

import com.example.gateway.model.BlacklistToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.Date
import java.util.UUID

interface BlacklistTokenRepository : ReactiveCrudRepository<BlacklistToken, UUID> {
    fun existsByToken(token: String): Mono<Boolean>

    fun deleteAllByExpireAtBefore(now: Date = Date()): Mono<Void>
}
