package com.example.payment.controller

import com.example.payment.dto.request.PaymentRequest
import com.example.payment.dto.response.PaymentResponse
import com.example.payment.service.UMoneyPaymentService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер, отвечающий за обработку запросов на получение ссылки на оплату.
 */
@RestController
@RequestMapping("/payment")
class PaymentController(private val UMoneyPaymentService: UMoneyPaymentService) {
    /**
     * Метод для получения ссылки на оплату.
     * @param request Запрос на получение ссылки на оплату.
     */
    @PostMapping
    fun getPaymentLink(@RequestBody @Valid request: PaymentRequest): Mono<PaymentResponse> {
        return UMoneyPaymentService.getPaymentLink(request)
    }
}
