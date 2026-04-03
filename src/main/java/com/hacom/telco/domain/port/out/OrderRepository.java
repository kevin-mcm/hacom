package com.hacom.telco.domain.port.out;

import com.hacom.telco.domain.model.Order;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface OrderRepository {
    Mono<Order> save(Order order);
    Mono<Order> findByOrderId(String orderId);
    Mono<Long> countByDateRange(OffsetDateTime from, OffsetDateTime to);
}
