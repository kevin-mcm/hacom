package com.hacom.telco.application.service;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.out.OrderRepository;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    static ActorSystem system;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Counter ordersProcessedCounter;

    @BeforeAll
    static void setup() {
        system = ActorSystem.create("test-system");
    }

    @AfterAll
    static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    void getOrderStatus_shouldReturnStatus_whenOrderExists() {
        OrderService orderService = new OrderService(orderRepository, system, ordersProcessedCounter);

        when(orderRepository.findByOrderId("order-123"))
                .thenReturn(Mono.just(Order.builder()
                        .orderId("order-123")
                        .status("PROCESSED")
                        .build()));

        StepVerifier.create(orderService.getOrderStatus("order-123"))
                .expectNext("PROCESSED")
                .verifyComplete();

        verify(orderRepository).findByOrderId("order-123");
    }

    @Test
    void getOrderStatus_shouldReturnEmpty_whenOrderNotFound() {
        OrderService orderService = new OrderService(orderRepository, system, ordersProcessedCounter);

        when(orderRepository.findByOrderId("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrderStatus("unknown"))
                .verifyComplete();
    }

    @Test
    void countOrdersByDateRange_shouldReturnCount() {
        OrderService orderService = new OrderService(orderRepository, system, ordersProcessedCounter);
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        when(orderRepository.countByDateRange(from, to)).thenReturn(Mono.just(5L));

        StepVerifier.create(orderService.countOrdersByDateRange(from, to))
                .expectNext(5L)
                .verifyComplete();

        verify(orderRepository).countByDateRange(from, to);
    }
}
