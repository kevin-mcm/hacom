package com.hacom.telco.infrastructure.adapter.mongodb;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public interface OrderReactiveRepository extends ReactiveMongoRepository<OrderDocument, String> {
    Mono<OrderDocument> findByOrderId(String orderId);

    @Query(count = true, value = "{ 'ts': { $gte: ?0, $lte: ?1 } }")
    Mono<Long> countByTsBetween(OffsetDateTime from, OffsetDateTime to);
}
