package com.hacom.telco.infrastructure.adapter.grpc;

import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.in.OrderUseCase;
import com.hacom.telco.domain.port.out.SmsPort;
import com.hacom.telco.grpc.OrderRequest;
import com.hacom.telco.grpc.OrderResponse;
import com.hacom.telco.grpc.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderUseCase orderUseCase;
    private final SmsPort smsPort;

    @Override
    public void insertOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        log.info("gRPC insertOrder called for orderId: {}", request.getOrderId());

        List<String> items = request.getItemsList();

        Order order = Order.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .customerPhoneNumber(request.getCustomerPhoneNumber())
                .items(items)
                .build();

        orderUseCase.processOrder(order)
                .subscribe(
                        processedOrder -> {
                            log.info("Order {} processed, sending SMS", processedOrder.getOrderId());
                            try {
                                smsPort.sendSms(
                                        processedOrder.getCustomerPhoneNumber(),
                                        "Your order " + processedOrder.getOrderId() + " has been processed"
                                );
                            } catch (Exception e) {
                                log.warn("Failed to send SMS for order {}: {} ({})", processedOrder.getOrderId(), e.getMessage(), e.getClass().getSimpleName());
                                log.debug("SMS send failure details", e);
                            }

                            OrderResponse response = OrderResponse.newBuilder()
                                    .setOrderId(processedOrder.getOrderId())
                                    .setStatus(processedOrder.getStatus())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("Error processing order: {}", error.getMessage(), error);
                            responseObserver.onError(error);
                        }
                );
    }
}
