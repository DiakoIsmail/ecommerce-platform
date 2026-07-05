package com.diako.order_service.repository;

import com.diako.order_service.config.TestcontainersConfiguration;
import com.diako.order_service.entity.Order;
import com.diako.order_service.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void savesAndRetrievesOrder() {
        Order saved = orderRepository.save(new Order(1L, 3, new BigDecimal("89.70")));

        Order found = orderRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getProductId()).isEqualTo(1L);
        assertThat(found.getTotalPrice()).isEqualByComparingTo("89.70");
        assertThat(found.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(found.getCreatedAt()).isNotNull();
    }
}