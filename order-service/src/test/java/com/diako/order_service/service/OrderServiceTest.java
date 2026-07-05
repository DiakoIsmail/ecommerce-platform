package com.diako.order_service.service;

import com.diako.order_service.client.ProductClient;
import com.diako.order_service.client.ProductClientResponse;
import com.diako.order_service.dto.OrderRequest;
import com.diako.order_service.dto.OrderResponse;
import com.diako.order_service.entity.Order;
import com.diako.order_service.entity.OrderStatus;
import com.diako.order_service.exception.OrderNotFoundException;
import com.diako.order_service.exception.ProductReferenceNotFoundException;
import com.diako.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create_fetchesProductAndSavesOrderWithComputedTotal() {
        when(productClient.getProduct(1L))
                .thenReturn(new ProductClientResponse(1L, "Desk Lamp", new BigDecimal("29.90")));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(new OrderRequest(1L, 3));

        assertThat(response.totalPrice()).isEqualByComparingTo("89.70");
        assertThat(response.productId()).isEqualTo(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_throwsProductReferenceNotFound_whenProductClientReturns404() {
        when(productClient.getProduct(999999L)).thenThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null));

        assertThatThrownBy(() -> orderService.create(new OrderRequest(999999L, 1)))
                .isInstanceOf(ProductReferenceNotFoundException.class)
                .hasMessageContaining("999999");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void findById_returnsOrder_whenItExists() {
        Order order = new Order(1L, 2, new BigDecimal("59.80"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(1L);

        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void findById_throwsNotFound_whenOrderMissing() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_mapsAllOrdersToResponses() {
        when(orderRepository.findAll()).thenReturn(List.of(
                new Order(1L, 1, new BigDecimal("10.00")),
                new Order(2L, 5, new BigDecimal("50.00"))
        ));

        assertThat(orderService.findAll())
                .hasSize(2)
                .extracting(OrderResponse::productId)
                .containsExactly(1L, 2L);
    }
}