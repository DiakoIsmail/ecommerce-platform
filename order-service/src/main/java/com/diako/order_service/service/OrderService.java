package com.diako.order_service.service;

import com.diako.order_service.client.ProductClient;
import com.diako.order_service.client.ProductClientResponse;
import com.diako.order_service.dto.OrderRequest;
import com.diako.order_service.dto.OrderResponse;
import com.diako.order_service.entity.Order;
import com.diako.order_service.event.OrderCreatedEvent;
import com.diako.order_service.exception.OrderNotFoundException;
import com.diako.order_service.exception.ProductReferenceNotFoundException;
import com.diako.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, ProductClient productClient,
                        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public OrderResponse create(OrderRequest request) {
        ProductClientResponse product = fetchProduct(request.productId());
        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.quantity()));

        Order saved = orderRepository.save(new Order(request.productId(), request.quantity(), totalPrice));
        log.info("Created order id={} productId={} quantity={} totalPrice={}",
                saved.getId(), saved.getProductId(), saved.getQuantity(), totalPrice);

        kafkaTemplate.send(ORDER_CREATED_TOPIC,
                new OrderCreatedEvent(saved.getId(), saved.getProductId(), saved.getQuantity(), saved.getTotalPrice()));
        log.info("Published OrderCreatedEvent for order id={}", saved.getId());

        return toResponse(saved);
    }

    private ProductClientResponse fetchProduct(Long productId) {
        try {
            return productClient.getProduct(productId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductReferenceNotFoundException(productId);
        }
    }

    private Order getOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getProductId(), order.getQuantity(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
    }
}