package com.example.gateway.controller

import com.example.gateway.dto.request.security.VerifyDto
import com.example.gateway.dto.response.EmptyResponse
import com.example.gateway.service.VerifyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@Tag(name = "Безопасность")
@RequestMapping("/api/v1/user/verify")
class VerifyController(private val verifyService: VerifyService) {
    /**
     * Подтвердить пользователя по токену из ссылки в письме.
     *
     * @param token Токен подтверждения.
     * @return Моно с EmptyResponse.
     */
    @GetMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пользователь подтверждён",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmptyResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Пользователь не найден")
        ]
    )
    @Operation(summary = "Подтверждение регистрации", description = "Подтверждение регистрации пользователя.")
    fun verify(@RequestParam token: String): Mono<ResponseEntity<EmptyResponse>> {
        return verifyService.verify(token)
            .then(Mono.just(ResponseEntity.accepted().body(EmptyResponse("Аккаунт подтверждён"))))
    }

    /**
     * Запросить повторное письмо для подтверждения аккаунта.
     *
     * @param verifyDto Данные для подтверждения.
     * @return Моно с EmptyResponse.
     */
    @PostMapping("/verify")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пользователь подтверждён",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmptyResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Пользователь не найден"),
            ApiResponse(responseCode = "403", description = "Действие не подтверждено")
        ]
    )
    @Operation(
        summary = "Повторное подтверждение аккаунта",
        description = "Запросить повторное письмо для подтверждения аккаунта."
    )
    fun getVerifyEmail(@RequestBody @Valid verifyDto: VerifyDto): Mono<ResponseEntity<EmptyResponse>> {
        return verifyService.sendVerifyEmail(verifyDto)
            .then(Mono.just(ResponseEntity.accepted().body(EmptyResponse("Письмо отправлено"))))
    }
}
