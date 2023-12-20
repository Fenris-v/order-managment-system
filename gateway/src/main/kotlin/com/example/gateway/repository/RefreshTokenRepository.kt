package com.example.gateway.repository

import com.example.gateway.model.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.Date
import java.util.UUID

interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, UUID> {
    fun existsRefreshTokenByToken(token: String): Mono<Boolean>

    fun deleteAllByExpireAtBefore(now: Date = Date()): Mono<Void>
}
