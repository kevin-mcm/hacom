package com.hacom.telco.application.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hacom.telco.domain.model.Order;
import com.hacom.telco.domain.port.out.OrderRepository;
import lombok.extern.log4j.Log4j2;

import java.time.OffsetDateTime;
import java.util.UUID;

@Log4j2
public class OrderProcessorActor extends AbstractActor {

    private final OrderRepository orderRepository;

    public OrderProcessorActor(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public static Props props(OrderRepository orderRepository) {
        return Props.create(OrderProcessorActor.class, orderRepository);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, this::processOrder)
                .build();
    }

    private void processOrder(Order order) {
        log.info("Actor processing order: {}", order.getOrderId());
        akka.actor.ActorRef sender = getSender();

        Order orderToSave = Order.builder()
                .orderId(order.getOrderId() != null ? order.getOrderId() : UUID.randomUUID().toString())
                .customerId(order.getCustomerId())
                .customerPhoneNumber(order.getCustomerPhoneNumber())
                .status("PROCESSED")
                .items(order.getItems())
                .ts(OffsetDateTime.now())
                .build();

        orderRepository.save(orderToSave)
                .subscribe(
                        savedOrder -> {
                            log.info("Order {} saved successfully", savedOrder.getOrderId());
                            sender.tell(savedOrder, getSelf());
                        },
                        error -> {
                            log.error("Failed to save order: {}", error.getMessage(), error);
                            Order failedOrder = Order.builder()
                                    .orderId(orderToSave.getOrderId())
                                    .status("FAILED")
                                    .build();
                            sender.tell(failedOrder, getSelf());
                        }
                );
    }
}
