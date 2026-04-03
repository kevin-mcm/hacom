package com.hacom.telco.infrastructure.adapter.web;

import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.in.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Development endpoint for inserting sample orders into MongoDB.
 *
 * <p><strong> LOCAL / DEV USE ONLY — Not available in production.</strong></p>
 *
 * <p>This controller is active only when the active Spring profile is
 * <em>not</em> {@code prod}. It can be invoked as:</p>
 * <pre>
 *   POST http://localhost:9898/orders/test/seed
 * </pre>
 */
@Profile("!prod")
@Log4j2
@RestController
@RequestMapping("/orders/test")
@RequiredArgsConstructor
public class SeedController {

    private final OrderUseCase orderUseCase;

    /**
     * Inserts sample orders into MongoDB and returns the list of created orders.
     *
     * @return list of inserted orders
     */
    @PostMapping("/seed")
    public Mono<ResponseEntity<List<Order>>> seedOrders() {
        log.warn("DEV ONLY — seeding sample orders via REST endpoint");
        return Flux.fromIterable(buildSampleOrders())
                .flatMap(orderUseCase::processOrder)
                .collectList()
                .map(ResponseEntity::ok);
    }

    private List<Order> buildSampleOrders() {
        return List.of(
                Order.builder()
                        .orderId("ORD-SEED-001")
                        .customerId("CUST-001")
                        .customerPhoneNumber("+52-55-1234-5678")
                        .status("PENDING")
                        .items(List.of("Plan 5GB", "Roaming Add-on"))
                        .ts(OffsetDateTime.parse("2024-01-15T10:00:00Z"))
                        .build(),
                Order.builder()
                        .orderId("ORD-SEED-002")
                        .customerId("CUST-002")
                        .customerPhoneNumber("+52-55-9876-5432")
                        .status("PROCESSED")
                        .items(List.of("Plan 10GB", "SMS Bundle"))
                        .ts(OffsetDateTime.parse("2024-01-16T14:30:00Z"))
                        .build(),
                Order.builder()
                        .orderId("ORD-SEED-003")
                        .customerId("CUST-003")
                        .customerPhoneNumber("+52-55-4455-6677")
                        .status("FAILED")
                        .items(List.of("Plan 1GB"))
                        .ts(OffsetDateTime.parse("2024-01-17T08:00:00Z"))
                        .build()
        );
    }
}
