package com.hacom.telco.infrastructure.adapter.mongodb;

import com.hacom.telco.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoOrderRepositoryTest {

    @Mock
    private OrderReactiveRepository reactiveRepository;

    @InjectMocks
    private MongoOrderRepository mongoOrderRepository;

    @Test
    void save_shouldReturnSavedOrder() {
        OrderDocument savedDoc = OrderDocument.builder()
                .orderId("order-1")
                .customerId("cust-1")
                .customerPhoneNumber("+1234567890")
                .status("PROCESSED")
                .items(List.of("item1"))
                .ts(OffsetDateTime.now())
                .build();

        when(reactiveRepository.save(any(OrderDocument.class))).thenReturn(Mono.just(savedDoc));

        Order order = Order.builder()
                .orderId("order-1")
                .customerId("cust-1")
                .customerPhoneNumber("+1234567890")
                .status("PROCESSED")
                .items(List.of("item1"))
                .ts(OffsetDateTime.now())
                .build();

        StepVerifier.create(mongoOrderRepository.save(order))
                .expectNextMatches(o -> "order-1".equals(o.getOrderId()) && "PROCESSED".equals(o.getStatus()))
                .verifyComplete();
    }

    @Test
    void findByOrderId_shouldReturnOrder_whenExists() {
        OrderDocument doc = OrderDocument.builder()
                .orderId("order-1")
                .status("PROCESSED")
                .build();

        when(reactiveRepository.findByOrderId("order-1")).thenReturn(Mono.just(doc));

        StepVerifier.create(mongoOrderRepository.findByOrderId("order-1"))
                .expectNextMatches(o -> "order-1".equals(o.getOrderId()))
                .verifyComplete();
    }

    @Test
    void findByOrderId_shouldReturnEmpty_whenNotExists() {
        when(reactiveRepository.findByOrderId("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(mongoOrderRepository.findByOrderId("unknown"))
                .verifyComplete();
    }

    @Test
    void countByDateRange_shouldReturnCount() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        when(reactiveRepository.countByTsBetween(from, to)).thenReturn(Mono.just(3L));

        StepVerifier.create(mongoOrderRepository.countByDateRange(from, to))
                .expectNext(3L)
                .verifyComplete();
    }
}
