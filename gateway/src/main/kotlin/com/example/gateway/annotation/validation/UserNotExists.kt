package com.example.gateway.annotation.validation

import com.example.gateway.validator.UserNotExistsValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [UserNotExistsValidator::class])
annotation class UserNotExists(
    val message: String = "User with same email already exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<Any>> = []
)
