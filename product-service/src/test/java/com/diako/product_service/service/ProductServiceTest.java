package com.diako.product_service.service;

import com.diako.product_service.dto.ProductRequest;
import com.diako.product_service.dto.ProductResponse;
import com.diako.product_service.entity.Product;
import com.diako.product_service.exception.ProductNotFoundException;
import com.diako.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findById_returnsProduct_whenItExists() {
        Product product = new Product("Coffee Mug", "Ceramic, 350ml", new BigDecimal("12.99"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(1L);

        assertThat(response.name()).isEqualTo("Coffee Mug");
        assertThat(response.price()).isEqualByComparingTo("12.99");
    }

    @Test
    void findById_throwsNotFound_whenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesProductAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Notebook", "A5, dotted", new BigDecimal("4.50"));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.create(request);

        assertThat(response.name()).isEqualTo("Notebook");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void findAll_mapsAllProductsToResponses() {
        when(productRepository.findAll()).thenReturn(List.of(
                new Product("Mug", "desc", new BigDecimal("10.00")),
                new Product("Pen", "desc", new BigDecimal("2.00"))
        ));

        assertThat(productService.findAll())
                .hasSize(2)
                .extracting(ProductResponse::name)
                .containsExactly("Mug", "Pen");
    }

    @Test
    void delete_throwsNotFound_andNeverCallsDelete_whenProductMissing() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(5L)).isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void update_updatesFieldsAndReturnsResponse_whenProductExists() {
        Product existing = new Product("Old Name", "Old description", new BigDecimal("5.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductRequest updateRequest = new ProductRequest("New Name", "New description", new BigDecimal("9.99"));
        ProductResponse response = productService.update(1L, updateRequest);

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.description()).isEqualTo("New description");
        assertThat(response.price()).isEqualByComparingTo("9.99");
    }

    @Test
    void update_throwsNotFound_whenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        ProductRequest request = new ProductRequest("Name", "Desc", new BigDecimal("1.00"));

        assertThatThrownBy(() -> productService.update(99L, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_removesProduct_whenItExists() {
        Product existing = new Product("Desk Lamp", "LED", new BigDecimal("29.90"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.delete(1L);

        verify(productRepository).delete(existing);
    }
}