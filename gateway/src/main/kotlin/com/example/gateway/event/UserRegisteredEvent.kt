package com.example.gateway.event

import com.example.gateway.model.User
import org.springframework.context.ApplicationEvent

/**
 * Событие о регистрации пользователя.
 */
class UserRegisteredEvent(source: Any, val user: User) : ApplicationEvent(source)
