package com.hacom.telco.application.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import com.hacom.telco.application.actor.OrderProcessorActor;
import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.in.OrderUseCase;
import com.hacom.telco.domain.port.out.OrderRepository;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class OrderService implements OrderUseCase {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final OrderRepository orderRepository;
    private final ActorRef orderProcessorActor;
    private final Counter ordersProcessedCounter;

    public OrderService(OrderRepository orderRepository,
                        ActorSystem actorSystem,
                        Counter ordersProcessedCounter) {
        this.orderRepository = orderRepository;
        this.ordersProcessedCounter = ordersProcessedCounter;
        this.orderProcessorActor = actorSystem.actorOf(
                OrderProcessorActor.props(orderRepository),
                "order-processor-" + counter.incrementAndGet()
        );
    }

    @Override
    public Mono<Order> processOrder(Order order) {
        log.info("Processing order: {}", order.getOrderId());
        ordersProcessedCounter.increment();
        return Mono.fromCompletionStage(
                Patterns.ask(orderProcessorActor, order, Duration.ofSeconds(30))
                        .toCompletableFuture()
                        .thenApply(result -> (Order) result)
        );
    }

    @Override
    public Mono<String> getOrderStatus(String orderId) {
        log.info("Getting status for order: {}", orderId);
        return orderRepository.findByOrderId(orderId)
                .map(Order::getStatus)
                .doOnSuccess(status -> log.debug("Order {} status: {}", orderId, status));
    }

    @Override
    public Mono<Long> countOrdersByDateRange(OffsetDateTime from, OffsetDateTime to) {
        log.info("Counting orders from {} to {}", from, to);
        return orderRepository.countByDateRange(from, to);
    }
}
