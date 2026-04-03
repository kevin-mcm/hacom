package com.hacom.telco.infrastructure.adapter.web;

import com.hacom.telco.domain.port.in.OrderUseCase;
import com.hacom.telco.infrastructure.adapter.web.dto.OrderCountResponse;
import com.hacom.telco.infrastructure.adapter.web.dto.OrderStatusResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @Test
    void getOrderStatus_shouldReturn200_whenOrderFound() {
        when(orderUseCase.getOrderStatus("order-123")).thenReturn(Mono.just("PROCESSED"));

        webTestClient.get()
                .uri("/orders/order-123/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderStatusResponse.class)
                .value(response -> {
                    assert response.orderId().equals("order-123");
                    assert response.status().equals("PROCESSED");
                });
    }

    @Test
    void getOrderStatus_shouldReturn404_whenOrderNotFound() {
        when(orderUseCase.getOrderStatus("unknown")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/orders/unknown/status")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void countOrders_shouldReturn200_withCount() {
        when(orderUseCase.countOrdersByDateRange(any(), any())).thenReturn(Mono.just(10L));

        webTestClient.get()
                .uri("/orders/count?from=2024-01-01T00:00:00Z&to=2024-12-31T23:59:59Z")
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderCountResponse.class)
                .value(response -> {
                    assert response.count() == 10L;
                });
    }
}
