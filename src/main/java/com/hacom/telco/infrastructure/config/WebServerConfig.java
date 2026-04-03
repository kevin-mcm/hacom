package com.hacom.telco.infrastructure.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class WebServerConfig {

    @Value("${apiPort}")
    private int apiPort;

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            log.info("Configuring WebFlux server port: {}", apiPort);
            factory.setPort(apiPort);
        };
    }
}
