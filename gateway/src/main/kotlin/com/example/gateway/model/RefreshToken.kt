package com.example.gateway.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * Класс представляет сущность токена обновления.
 */
@Table(name = "refresh_tokens")
data class RefreshToken(
    var id: UUID? = null,
    var userId: Long? = null,
    var accessId: UUID? = null,
    var expireAt: LocalDateTime? = null,

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
