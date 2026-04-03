package com.hacom.telco.infrastructure.adapter.web;

import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.in.OrderUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(SeedController.class)
@ActiveProfiles("dev")
class SeedControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @Test
    void seedOrders_shouldReturn200_withInsertedOrders() {
        Order sampleOrder = Order.builder()
                .orderId("ORD-SEED-001")
                .customerId("CUST-001")
                .customerPhoneNumber("+52-55-1234-5678")
                .status("PENDING")
                .items(List.of("Plan 5GB", "Roaming Add-on"))
                .ts(OffsetDateTime.parse("2024-01-15T10:00:00Z"))
                .build();

        when(orderUseCase.processOrder(any(Order.class))).thenReturn(Mono.just(sampleOrder));

        webTestClient.post()
                .uri("/orders/test/seed")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .hasSize(3);

        verify(orderUseCase, times(3)).processOrder(any(Order.class));
    }
}
