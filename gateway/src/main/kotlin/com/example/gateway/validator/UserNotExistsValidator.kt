package com.example.gateway.validator

import com.example.gateway.annotation.validation.UserNotExists
import com.example.gateway.repository.UserRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

/**
 * Осуществляет валидацию входных данных.
 * <p>
 * Проверяет существование пользователя с указанным email в БД.
 */
class UserNotExistsValidator(private val userRepository: UserRepository) : ConstraintValidator<UserNotExists, String> {

    /**
     * Проверяет существование пользователя с указанным email в БД.
     *
     * @param value Валидируемое значение.
     * @param context Контекст валидатора.
     * @return Существует ли пользовать в БД.
     */
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return !userRepository.existsByEmailIgnoreCase(value).toFuture().get()
    }
}
