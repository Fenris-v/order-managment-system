package com.example.inventory.grpc.server.service

import com.example.inventory.service.ProductService
import com.example.product.ProductServiceGrpc.ProductServiceImplBase
import com.example.product.ShortProduct
import com.example.product.ShortProductsRequest
import io.grpc.Status
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

/**
 * Сервис для работы с продуктами.
 */
@GrpcService
class GrpcProductService(private val productService: ProductService) : ProductServiceImplBase() {

    /**
     * Запрашивает через gRPC список продуктов по их идентификаторам и возвращает их в ответе.
     *
     * @param request Список идентификаторов продуктов
     * @param responseObserver Обработчик ответа
     */
    override fun getProductsByIdList(
        request: ShortProductsRequest,
        responseObserver: StreamObserver<ShortProduct>
    ) {
        val idsList: List<Long> = request.idsList
        productService.getProductsByIdList(idsList)
            .subscribe(
                { product ->
                    responseObserver.onNext(
                        ShortProduct.newBuilder()
                            .setId(product.id)
                            .setTitle(product.title)
                            .build()
                    )
                },
                { e ->
                    responseObserver.onError(
                        Status.INTERNAL
                            .withDescription("Failed to retrieve products: ${e.message}")
                            .asRuntimeException()
                    )
                },
                { responseObserver.onCompleted() })
    }
}
