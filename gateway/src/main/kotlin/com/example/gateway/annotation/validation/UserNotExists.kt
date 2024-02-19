package com.example.gateway.annotation.validation

import com.example.gateway.validator.UserNotExistsValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

/**
 * Аннотация для проверки существования пользователя в БД по email. Используется в таких ситуациях, как регистрация
 * нового пользователя.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [UserNotExistsValidator::class])
annotation class UserNotExists(
    val message: String = "User with same email already exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<Any>> = []
)
