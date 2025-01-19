package com.example.gateway.repository

import com.example.gateway.model.RefreshToken
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

/**
 * Репозиторий для взаимодействия с таблицей refresh_tokens в базе данных.
 */
interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, UUID> {

    /**
     * Ищет обновляющий токен по его значению.
     *
     * @param id Идентификатор обновляющего токена, который необходимо найти.
     * @return Mono, возвращающий найденный обновляющий токен или null, если токен не найден.
     */
    @Query(value = "SELECT access_id FROM refresh_tokens as r WHERE r.id = :id")
    fun findAccessIdByTokenId(id: UUID): Mono<UUID>

    /**
     * Удаляет все обновляющие токены, срок действия которых истек до указанной даты.
     *
     * @param expireAt Дата, до которой истекает срок действия обновляющего токена.
     * @return Mono, завершающийся при успешном удалении обновляющих токенов.
     */
    fun deleteAllByExpireAtBefore(expireAt: LocalDateTime = LocalDateTime.now()): Mono<Unit>
}
