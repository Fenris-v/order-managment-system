package com.example.gateway.grpc.handler

import com.example.starter.utils.exception.EntityNotFoundException
import io.grpc.Status
import io.grpc.StatusRuntimeException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler

/**
 * Обработчик исключений для gRPC.
 */
@GrpcAdvice
class ExceptionHandler {
    /**
     * Обрабатывает исключение EntityNotFoundException и возвращает статус 5.
     *
     * @param exception Исключение при отсутствии сущности с заданными параметрами.
     * @return Ответ с кодом 5.
     */
    @GrpcExceptionHandler(EntityNotFoundException::class)
    fun handleInvalidArgumentException(exception: EntityNotFoundException): StatusRuntimeException {
        return Status.NOT_FOUND.withDescription(exception.message).asRuntimeException()
    }
}
