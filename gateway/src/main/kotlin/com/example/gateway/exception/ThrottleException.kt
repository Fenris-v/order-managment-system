package com.example.gateway.exception

/**
 * Исключение при попытке слишком частого повторения определённых действий.
 */
class ThrottleException(message: String = "Повторите действие позднее") : RuntimeException(message)
