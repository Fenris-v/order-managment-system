package com.example.order.grpc.client.service

import com.example.product.ProductServiceGrpc
import com.example.product.ShortProduct
import com.example.product.ShortProductsRequest
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

/**
 * gRPC клиент сервиса продуктов.
 */
@Service
class ProductService {

    @GrpcClient("productServer")
    private val stub: ProductServiceGrpc.ProductServiceStub? = null

    /**
     * Запрашивает продукты по их идентификаторам.
     *
     * @param ids Идентификаторы продуктов
     * @return Продукты
     */
    fun getProducts(ids: List<Long>): Flux<ShortProduct> {
        val request = ShortProductsRequest.newBuilder().addAllIds(ids).build()

        return Flux.create { emitter ->
            stub!!.getProductsByIdList(request, object : StreamObserver<ShortProduct> {
                override fun onNext(product: ShortProduct) {
                    emitter.next(product)
                }

                override fun onError(t: Throwable) {
                    emitter.error(t)
                }

                override fun onCompleted() {
                    emitter.complete()
                }
            })
        }
    }
}
