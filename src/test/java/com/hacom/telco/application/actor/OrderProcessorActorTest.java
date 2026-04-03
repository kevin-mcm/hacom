package com.hacom.telco.application.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.out.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessorActorTest {

    static ActorSystem system;

    @Mock
    OrderRepository orderRepository;

    @BeforeAll
    static void setup() {
        system = ActorSystem.create("test-actor-system");
    }

    @AfterAll
    static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    void actor_shouldProcessOrder_andReplyWithProcessedStatus() {
        new TestKit(system) {{
            Order savedOrder = Order.builder()
                    .orderId("order-1")
                    .customerId("cust-1")
                    .customerPhoneNumber("+1234567890")
                    .status("PROCESSED")
                    .items(List.of("item1"))
                    .build();

            when(orderRepository.save(any())).thenReturn(Mono.just(savedOrder));

            ActorRef actor = system.actorOf(OrderProcessorActor.props(orderRepository));

            Order inputOrder = Order.builder()
                    .orderId("order-1")
                    .customerId("cust-1")
                    .customerPhoneNumber("+1234567890")
                    .items(List.of("item1"))
                    .build();

            actor.tell(inputOrder, getRef());

            Order response = expectMsgClass(Duration.ofSeconds(5), Order.class);
            assert "PROCESSED".equals(response.getStatus());
        }};
    }
}
