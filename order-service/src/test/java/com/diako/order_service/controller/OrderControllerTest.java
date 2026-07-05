package com.diako.order_service.controller;

import com.diako.order_service.dto.OrderResponse;
import com.diako.order_service.entity.OrderStatus;
import com.diako.order_service.exception.ProductReferenceNotFoundException;
import com.diako.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private OrderService orderService;

    @Test
    void create_returns201_whenRequestIsValid() {
        when(orderService.create(any())).thenReturn(
                new OrderResponse(1L, 1L, 3, new BigDecimal("89.70"), OrderStatus.PENDING, LocalDateTime.now()));

        assertThat(mockMvcTester.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"productId": 1, "quantity": 3}
                                """))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"productId": 1, "quantity": 3, "totalPrice": 89.70}
                        """);
    }

    @Test
    void create_returns422_whenProductDoesNotExist() {
        when(orderService.create(any())).thenThrow(new ProductReferenceNotFoundException(999999L));

        assertThat(mockMvcTester.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"productId": 999999, "quantity": 1}
                                """))
                .hasStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void create_returns400_whenQuantityIsMissing() {
        assertThat(mockMvcTester.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"productId": 1}
                                """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getById_returns200_whenOrderExists() {
        when(orderService.findById(1L)).thenReturn(
                new OrderResponse(1L, 1L, 3, new BigDecimal("89.70"), OrderStatus.PENDING, LocalDateTime.now()));

        assertThat(mockMvcTester.get().uri("/api/orders/1")).hasStatusOk();
    }
}