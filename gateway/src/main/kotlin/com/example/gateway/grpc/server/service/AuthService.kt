package com.example.gateway.grpc.server.service

import com.example.gateway.repository.UserRepository
import com.example.security.AuthServiceGrpc
import com.example.security.UserRequest
import com.example.security.UserResponse
import com.example.starter.utils.exception.EntityNotFoundException
import com.example.starter.utils.utils.jwt.JwtUtils
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Mono

/**
 * Класс gRPC-сервиса для работы с пользователями.
 */
@GrpcService
class AuthService(private val jwtUtils: JwtUtils, private val userRepository: UserRepository) :
    AuthServiceGrpc.AuthServiceImplBase() {

    /**
     * Получение пользователя по JWT-токену или id.
     *
     * @param request Запрос на получение пользователя.
     * @param responseObserver Ответ на запрос.
     */
    override fun getUser(request: UserRequest, responseObserver: StreamObserver<UserResponse?>) {
        Mono.fromCallable { if (request.jwt.isNotBlank()) jwtUtils.extractAllClaims(request.jwt).id else request.id }
            .flatMap { userRepository.findById(it) }
            .switchIfEmpty(Mono.error(EntityNotFoundException()))
            .map {
                UserResponse.newBuilder()
                    .setId(it.id!!)
                    .setEmail(it.email)
                    .setName(it.name ?: "")
                    .setLastname(it.lastname ?: "")
                    .build()
            }
            .subscribe(
                {
                    responseObserver.onNext(it)
                    responseObserver.onCompleted()
                },
                { throwable ->
                    responseObserver.onError(throwable)
                }
            )
    }
}
