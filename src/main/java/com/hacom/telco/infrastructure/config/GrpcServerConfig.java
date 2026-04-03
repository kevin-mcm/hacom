package com.hacom.telco.infrastructure.config;

import com.hacom.telco.domain.port.in.OrderUseCase;
import com.hacom.telco.domain.port.out.SmsPort;
import com.hacom.telco.infrastructure.adapter.grpc.OrderGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Log4j2
@Configuration
public class GrpcServerConfig {

    @Value("${grpc.port:9090}")
    private int grpcPort;

    @Bean
    public OrderGrpcService orderGrpcService(OrderUseCase orderUseCase, SmsPort smsPort) {
        return new OrderGrpcService(orderUseCase, smsPort);
    }

    @Bean
    public SmartLifecycle grpcServer(OrderGrpcService orderGrpcService) {
        return new SmartLifecycle() {
            private Server server;
            private boolean running = false;

            @Override
            public void start() {
                try {
                    server = ServerBuilder.forPort(grpcPort)
                            .addService(orderGrpcService)
                            .build()
                            .start();
                    running = true;
                    log.info("gRPC server started on port {}", grpcPort);
                } catch (IOException e) {
                    log.error("Failed to start gRPC server: {}", e.getMessage(), e);
                }
            }

            @Override
            public void stop() {
                if (server != null) {
                    server.shutdown();
                    running = false;
                    log.info("gRPC server stopped");
                }
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }
}
