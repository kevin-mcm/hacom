package com.hacom.telco.infrastructure.adapter.web;

import com.hacom.telco.domain.port.in.OrderUseCase;
import com.hacom.telco.infrastructure.adapter.web.dto.OrderStatusResponse;
import com.hacom.telco.infrastructure.adapter.web.dto.OrderCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Log4j2
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @GetMapping("/{orderId}/status")
    public Mono<ResponseEntity<OrderStatusResponse>> getOrderStatus(@PathVariable String orderId) {
        log.info("REST request for order status: {}", orderId);
        return orderUseCase.getOrderStatus(orderId)
                .map(status -> ResponseEntity.ok(new OrderStatusResponse(orderId, status)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public Mono<ResponseEntity<OrderCountResponse>> countOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        log.info("REST request to count orders from {} to {}", from, to);
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' must not be after 'to'");
        }
        return orderUseCase.countOrdersByDateRange(from, to)
                .map(count -> ResponseEntity.ok(new OrderCountResponse(from, to, count)));
    }
}
