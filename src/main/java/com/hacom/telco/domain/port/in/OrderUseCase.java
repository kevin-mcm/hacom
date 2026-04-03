package com.hacom.telco.domain.port.in;

import com.hacom.telco.domain.model.Order;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface OrderUseCase {
    Mono<Order> processOrder(Order order);
    Mono<String> getOrderStatus(String orderId);
    Mono<Long> countOrdersByDateRange(OffsetDateTime from, OffsetDateTime to);
}
