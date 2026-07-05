package com.diako.notification_service.listener;

import com.diako.notification_service.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @KafkaListener(topics = "order-created", groupId = "notification-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: orderId={} productId={} quantity={} totalPrice={}",
                event.orderId(), event.productId(), event.quantity(), event.totalPrice());
        log.info("Simulating notification: confirmation email sent for order {}", event.orderId());
    }
}