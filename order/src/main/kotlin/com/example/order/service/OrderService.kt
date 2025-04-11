package com.example.order.service

import com.example.order.dto.request.OrderRequest
import com.example.order.dto.response.OrderDetailResponse
import com.example.order.dto.response.OrderResponse
import com.example.order.dto.response.Product
import com.example.order.grpc.client.service.ProductService
import com.example.order.mapper.OrderMapper
import com.example.order.model.Order
import com.example.order.producer.OrderProducer
import com.example.order.repository.OrderRepository
import com.example.product.ShortProduct
import com.example.starter.utils.dto.order.OrderProduct
import com.example.starter.utils.utils.jwt.JwtUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Сервис для работы с заказами.
 */
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val orderProducer: OrderProducer,
    private val orderMapper: OrderMapper,
    private val jwtUtils: JwtUtils
) {

    /**
     * Создание заказа.
     *
     * @param orderRequest Запрос на создание заказа
     * @param authorization Токен авторизации
     * @return Mono<OrderResponse>
     */
    fun createOrder(orderRequest: OrderRequest, authorization: String): Mono<OrderResponse> {
        return Mono.fromCallable { jwtUtils.extractAllClaims(authorization).id }
            .flatMap { userId -> Mono.fromCallable { orderMapper.mapOrderRequestToOrder(orderRequest, userId) } }
            .flatMap { order ->
                orderRepository.save(order)
                    .flatMap { Mono.fromCallable { orderProducer.process(it) } }
                    .map { OrderResponse(order.id, order.status) }
            }
    }

    /**
     * Получение списка заказов.
     *
     * @param authorization Токен авторизации
     * @return Mono<List<OrderResponse>>
     */
    fun getOrders(authorization: String): Mono<List<OrderResponse>> {
        return orderRepository.findAllByUserId(jwtUtils.extractAllClaims(authorization).id)
            .map { order -> OrderResponse(order.id, order.status) }
            .collectList()
    }

    /**
     * Получение заказа.
     *
     * @param authorization Токен авторизации
     * @param orderId Идентификатор заказа
     * @return Mono<OrderDetailResponse>
     */
    fun getOrder(authorization: String, orderId: UUID): Mono<OrderDetailResponse> {
        return orderRepository.findFirstByIdAndUserId(orderId, jwtUtils.extractAllClaims(authorization).id)
            .flatMap {
                val productIds: List<Long> = it.products.map { product -> product.productId!! }
                productService.getProducts(productIds)
                    .collectList()
                    .map { products -> createOrderWithProductNames(it, products) }
            }
    }

    private fun createOrderWithProductNames(order: Order, products: List<ShortProduct>): OrderDetailResponse {
        return OrderDetailResponse(
            order.id,
            order.userId,
            order.status,
            createProductsWithTitle(order.products, products),
            order.createdAt,
            order.updatedAt
        )
    }

    private fun createProductsWithTitle(
        orderProducts: List<OrderProduct>,
        products: List<ShortProduct>
    ): List<Product> {
        val result: MutableList<Product> = ArrayList()
        var product: Product
        for (orderProduct in orderProducts) {
            product = Product(
                orderProduct.productId,
                products.find { it.id == orderProduct.productId }?.title,
                orderProduct.amount,
                orderProduct.price
            )

            result.add(product)
        }

        return result
    }
}
