package com.hacom.telco.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter ordersProcessedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("orders.processed.total")
                .description("Total number of orders processed")
                .tag("service", "telco-order-service")
                .register(meterRegistry);
    }
}
