package com.example.gateway.exception

/**
 * Исключение, выбрасываемое, когда пользовательский ввод не прошёл те или иные проверки.
 */
class UnprocessableException(message: String) : RuntimeException(message)
