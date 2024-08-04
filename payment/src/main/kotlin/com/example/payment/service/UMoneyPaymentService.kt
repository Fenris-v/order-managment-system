package com.example.payment.service

import com.example.payment.dto.request.PaymentRequest
import com.example.payment.dto.response.PaymentResponse
import com.example.payment.dto.umoney.Amount
import com.example.payment.dto.umoney.request.Confirmation
import com.example.payment.dto.umoney.request.Customer
import com.example.payment.dto.umoney.request.PaymentMethodData
import com.example.payment.dto.umoney.request.Receipt
import com.example.payment.dto.umoney.request.UMoneyPaymentRequest
import com.example.payment.dto.umoney.request.UMoneyProduct
import com.example.payment.dto.umoney.response.UMoneyResponse
import com.example.payment.enums.CurrencyEnum
import com.example.payment.enums.PaymentTypeEnum
import com.example.payment.enums.UPaymentStatus
import com.example.payment.event.PaymentEvent
import com.example.payment.model.Transaction
import com.example.payment.repository.TransactionRepository
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

private val log: KLogger = KotlinLogging.logger {}

/**
 * Сервис для работы с API UMoney. Данный класс обеспечивает возможность оплата с помощью банковской карты, проверку
 * статуса платежей и обновление их статусов.
 */
@Service
class UMoneyPaymentService(
    @Value("\${app.config.vatCode}") private val vatCode: Int,
    @Value("\${app.config.uMoneyKey}") private val apiKey: String,
    @Value("\${app.config.uMoneyShopId}") private val shopId: String,
    @Value("\${app.config.uMoneyPayments}") private val paymentUrl: String,
    private val client: WebClient,
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher,
    private val transactionRepository: TransactionRepository
) {
    companion object {
        private const val PRODUCT_DESCRIPTION = "Пополнение баланса"
        private const val IDEMPOTENCY_KEY_HEADER = "Idempotence-Key"
        private const val DESCRIPTION = "Пополнение баланса пользователя"
        private const val RETURN_URL = "http://localhost:8001/payment/confirm"
    }

    /**
     * Запрашивает актуальный статус платежа по его идентификатору и возвращает его.
     *
     * @param paymentId идентификатор платежа
     */
    fun getTransactionStatus(paymentId: UUID): Mono<UMoneyResponse> {
        return client.get()
            .uri("$paymentUrl/$paymentId")
            .headers {
                it.setBasicAuth(shopId, apiKey)
            }
            .retrieve()
            .bodyToMono(UMoneyResponse::class.java)
    }

    /**
     * Отменяет платеж по его идентификатору.
     *
     * @param paymentId идентификатор платежа
     */
    @Transactional
    fun cancelTransaction(paymentId: UUID): Mono<Void> {
        return transactionRepository.updateStatusByPaymentId(UPaymentStatus.CANCELED.name, paymentId)
    }

    /**
     * Обрабатывает успешный платеж и обновляет его статус в базе данных.
     *
     * @param paymentId идентификатор платежа
     */
    @Transactional
    fun processSucceedTransaction(paymentId: UUID): Mono<Void> {
        return transactionRepository.findTransactionByPaymentId(paymentId)
            .flatMap { transaction ->
                transaction.status = UPaymentStatus.SUCCEEDED
                transactionRepository.save(transaction)
                    .zipWith(Mono.just(log.info { "Дописать логику пополнения баланса на ${transaction.amount}" })) // TODO: Дописать логику пополнения баланса
                    .then()
            }
    }

    /**
     * Отправляет запрос на создание платежа и возвращает его идентификатор.
     *
     * @param request данные для создания платежа
     */
    @Transactional
    fun getPaymentLink(request: PaymentRequest): Mono<PaymentResponse> {
        return userService.getUser()
            .flatMap { user ->
                val transaction: Transaction = createTransaction(request, user)
                executeUMoneyRequest(request, transaction, user)
                    .flatMap { uResponse ->
                        log.info { "Ответ от UMoney получен. Обработка ответа" }

                        transaction.paymentId = uResponse.id
                        transaction.status = UPaymentStatus.PENDING
                        transaction.createdAt = LocalDateTime.now()
                        transaction.updatedAt = transaction.createdAt

                        transactionRepository.save(transaction)
                            .thenReturn(PaymentResponse(uResponse.confirmation!!.confirmationUrl!!, uResponse.id))
                            .doOnSuccess { eventPublisher.publishEvent(PaymentEvent(uResponse.id)) }
                    }
            }
    }

    private fun createTransaction(request: PaymentRequest, user: User): Transaction {
        log.debug { "Создание транзакции" }
        return Transaction(
            userId = user.id,
            amount = request.amount,
            status = UPaymentStatus.PENDING,
            idempotenceKey = UUID.randomUUID()
        )
    }

    private fun executeUMoneyRequest(
        request: PaymentRequest,
        transaction: Transaction,
        user: User
    ): Mono<UMoneyResponse> {
        log.debug { "Запрос на создание платежа" }

        return client.post()
            .uri(paymentUrl)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers {
                it.setBasicAuth(shopId, apiKey)
                it.set(IDEMPOTENCY_KEY_HEADER, transaction.idempotenceKey.toString())
            }
            .body(BodyInserters.fromValue(getUMoneyPaymentRequest(request, user)))
            .retrieve()
            .bodyToMono(UMoneyResponse::class.java)
    }

    private fun getUMoneyPaymentRequest(request: PaymentRequest, user: User): UMoneyPaymentRequest =
        UMoneyPaymentRequest(
            Amount(request.amount),
            Receipt(
                Customer("${user.name} ${user.lastname}", user.email),
                listOf(UMoneyProduct(PRODUCT_DESCRIPTION, Amount(request.amount, CurrencyEnum.RUB), vatCode, 1))
            ),
            PaymentMethodData(PaymentTypeEnum.BANK_CARD.value),
            Confirmation(RETURN_URL),
            DESCRIPTION
        )
}
