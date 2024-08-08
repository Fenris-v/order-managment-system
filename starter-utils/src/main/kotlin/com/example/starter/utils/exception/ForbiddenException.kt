package com.example.starter.utils.exception

/**
 * Исключение, представляющее ситуацию, когда доступ к ресурсу запрещен.
 */
class ForbiddenException(message: String? = null) : RuntimeException(message)
