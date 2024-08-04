package com.example.payment.controller

import com.example.starter.utils.controller.AbstractExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice

/**
 * Глобальный обработчик исключений для контроллеров. Предоставляет обработку различных видов исключений и возвращает
 * соответствующие ответы с информацией об ошибке.
 */
@ControllerAdvice
class CustomAbstractExceptionHandler : AbstractExceptionHandler()
