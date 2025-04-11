package com.example.gateway.repository

import com.example.gateway.model.AccessToken
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Репозиторий для взаимодействия с таблицей access_tokens в базе данных.
 */
interface AccessTokenRepository : ReactiveCrudRepository<AccessToken, UUID> {

    /**
     * Проверяет существование токена по идентификатору.
     *
     * @param id Идентификатор токена.
     * @return Mono, возвращающий true, если токен существует, и false в противном случае.
     */
    @Query("SELECT EXISTS(SELECT id FROM access_tokens WHERE id = :id)")
    override fun existsById(id: UUID): Mono<Boolean>

    /**
     * Удаляет токен по его идентификатору.
     *
     * @param id Идентификатор токена, который необходимо удалить.
     * @return Mono, завершающийся при успешном удалении токена.
     */
    @Query(value = "DELETE FROM access_tokens WHERE id = :id")
    override fun deleteById(id: UUID): Mono<Void>

    /**
     * Удаляет все токены пользователя кроме токена с переданным идентификатором.
     *
     * @param id Идентификатор токена, который необходимо удалить.
     * @return Mono, завершающийся при успешном удалении токена.
     */
    @Query(value = "DELETE FROM access_tokens WHERE id <> :id")
    fun deleteWhereIdNot(id: UUID): Mono<Unit>

    /**
     * Удаляет все токены пользователя.
     *
     * @param userId Идентификатор пользователя, которому необходимо удалить все токены.
     * @return Mono, завершающийся при успешном удалении токена.
     */
    fun deleteAllByUserId(userId: Long): Mono<Unit>
}
