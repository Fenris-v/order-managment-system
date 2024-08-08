package com.example.payment.model

import com.example.payment.enums.UPaymentStatus
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * Класс представляет сущность платежа.
 */
@Table(name = "transactions")
data class Transaction(
    @Id
    @JsonProperty("id")
    val id: Long? = null,
    val userId: Long? = null,
    val amount: Double? = null,
    @Enumerated(EnumType.STRING)
    var status: UPaymentStatus? = null,
    var paymentId: UUID? = null,
    val idempotenceKey: UUID? = null,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
