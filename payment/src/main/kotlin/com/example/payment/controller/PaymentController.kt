package com.example.payment.controller

import com.example.payment.dto.request.PaymentRequest
import com.example.payment.dto.response.HistoryResponse
import com.example.payment.dto.response.PaymentResponse
import com.example.payment.service.UMoneyPaymentService
import com.example.starter.utils.dto.response.ExceptionDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер, отвечающий за обработку запросов на получение ссылки на оплату.
 */
@RestController
@RequestMapping("/payment")
class PaymentController(private val uMoneyPaymentService: UMoneyPaymentService) {

    /**
     * Метод для получения ссылки на оплату.
     * @param request Запрос на получение ссылки на оплату.
     */
    @PostMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Платёж создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = PaymentResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            ApiResponse(
                responseCode = "422",
                description = "Ошибка валидации",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDto::class)
                )]
            )
        ]
    )
    @Operation(summary = "Новый платёж", description = "Создание нового платёжа.")
    fun getPaymentLink(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody @Valid request: PaymentRequest
    ): Mono<PaymentResponse> {
        return uMoneyPaymentService.getPaymentLink(authorization, request)
    }

    /**
     * Метод для получения истории платежей.
     *
     * @param page Номер страницы.
     * @param size Количество элементов на странице.
     * @return История платежей.
     */
    @GetMapping("/history")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "История платежей",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = HistoryResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "История платежей", description = "Получение истории платежей.")
    fun getHistory(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int
    ): Mono<HistoryResponse> {
        return uMoneyPaymentService.getHistory(authorization, page, size)
    }
}
