package com.hacom.telco.infrastructure.config;

import akka.actor.ActorSystem;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class ActorSystemConfig {

    @Bean
    public ActorSystem actorSystem() {
        log.info("Creating Akka ActorSystem");
        return ActorSystem.create("telco-actor-system");
    }
}
