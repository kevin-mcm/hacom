package com.hacom.telco.infrastructure.adapter.mongodb;

import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class MongoOrderRepository implements OrderRepository {

    private final OrderReactiveRepository reactiveRepository;

    @Override
    public Mono<Order> save(Order order) {
        log.debug("Saving order {} to MongoDB", order.getOrderId());
        OrderDocument document = toDocument(order);
        return reactiveRepository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Mono<Order> findByOrderId(String orderId) {
        log.debug("Finding order {} in MongoDB", orderId);
        return reactiveRepository.findByOrderId(orderId)
                .map(this::toDomain);
    }

    @Override
    public Mono<Long> countByDateRange(OffsetDateTime from, OffsetDateTime to) {
        log.debug("Counting orders between {} and {}", from, to);
        return reactiveRepository.countByTsBetween(from, to);
    }

    private OrderDocument toDocument(Order order) {
        return OrderDocument.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .customerPhoneNumber(order.getCustomerPhoneNumber())
                .status(order.getStatus())
                .items(order.getItems())
                .ts(order.getTs())
                .build();
    }

    private Order toDomain(OrderDocument document) {
        return Order.builder()
                .orderId(document.getOrderId())
                .customerId(document.getCustomerId())
                .customerPhoneNumber(document.getCustomerPhoneNumber())
                .status(document.getStatus())
                .items(document.getItems())
                .ts(document.getTs())
                .build();
    }
}
