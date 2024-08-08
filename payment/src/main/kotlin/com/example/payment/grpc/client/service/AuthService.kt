package com.example.payment.grpc.client.service

import com.example.security.AuthServiceGrpc
import com.example.security.UserRequest
import com.example.security.UserResponse
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

/**
 * Класс gRPC-клиент для вызова процедур связанных с пользователем.
 */
@Service
class AuthService {
    @GrpcClient("securityServer")
    private val stub: AuthServiceGrpc.AuthServiceBlockingStub? = null

    /**
     * Поиск пользователя по JWT-токену.
     *
     * @param jwt JWT-токен
     * @return Пользователь
     */
    fun getUser(jwt: String): UserResponse {
        return stub!!.getUser(UserRequest.newBuilder().setJwt(jwt).build())
    }
}
