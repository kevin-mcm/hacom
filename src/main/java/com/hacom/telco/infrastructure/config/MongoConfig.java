package com.hacom.telco.infrastructure.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.List;

@Log4j2
@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.hacom.telco.infrastructure.adapter.mongodb")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${mongodbUri}")
    private String mongodbUri;

    @Value("${mongodbDatabase}")
    private String mongodbDatabase;

    @Override
    public MongoClient reactiveMongoClient() {
        log.info("Configuring MongoDB client: {}", mongodbUri);
        return MongoClients.create(mongodbUri);
    }

    @Override
    protected String getDatabaseName() {
        return mongodbDatabase;
    }

    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new OffsetDateTimeWriteConverter(),
                new OffsetDateTimeReadConverter()
        ));
    }
}
