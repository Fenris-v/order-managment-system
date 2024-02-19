package com.example.gateway.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Класс исключения, которое вызывается при попытке доступа к ресурсу без прав доступа.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException : RuntimeException()
