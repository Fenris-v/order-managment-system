package com.example.payment.controller

import com.example.payment.dto.response.UserBalanceResponse
import com.example.payment.service.UserBalanceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер для работы с балансом пользователя.
 */
@RestController
@RequestMapping("/user/balance")
class UserBalanceController(private val userBalanceService: UserBalanceService) {
    /**
     * Метод для получения баланса пользователя.
     *
     * @param authorization Токен авторизации.
     * @return Баланс пользователя.
     */
    @GetMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Баланс пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserBalanceResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "Получение баланса", description = "Получение баланса пользователя.")
    fun getBalance(@RequestHeader("Authorization") authorization: String): Mono<UserBalanceResponse> {
        return userBalanceService.getBalance(authorization)
    }
}
