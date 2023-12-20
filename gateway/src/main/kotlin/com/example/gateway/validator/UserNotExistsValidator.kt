package com.example.gateway.validator

import com.example.gateway.annotation.validation.UserNotExists
import com.example.gateway.repository.UserRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UserNotExistsValidator(private val userRepository: UserRepository) : ConstraintValidator<UserNotExists, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return !userRepository.existsByEmail(value).toFuture().get()
    }
}
