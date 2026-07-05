package com.diako.product_service.controller;

import com.diako.product_service.dto.ProductResponse;
import com.diako.product_service.exception.ProductNotFoundException;
import com.diako.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private ProductService productService;

    @Test
    void getById_returns200AndProductJson() {
        when(productService.findById(1L))
                .thenReturn(new ProductResponse(1L, "Coffee Mug", "Ceramic, 350ml", new BigDecimal("12.99")));

        assertThat(mockMvcTester.get().uri("/api/products/1"))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"id": 1, "name": "Coffee Mug", "price": 12.99}
                        """);
    }

    @Test
    void getById_returns404_whenMissing() {
        when(productService.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        assertThat(mockMvcTester.get().uri("/api/products/99"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .isLenientlyEqualTo("""
                    {"title": "Product not found"}
                    """);
    }


    @Test
    void create_returns201_whenRequestIsValid() {
        when(productService.create(any()))
                .thenReturn(new ProductResponse(2L, "Notebook", "A5, dotted", new BigDecimal("4.50")));

        assertThat(mockMvcTester.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name": "Notebook", "description": "A5, dotted", "price": 4.50}
                                """))
                .hasStatus(HttpStatus.CREATED);
    }

    @Test
    void create_returns400_whenNameIsBlank() {
        assertThat(mockMvcTester.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name": "", "price": 4.50}
                                """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void update_returns200AndUpdatedProduct_whenValid() {
        when(productService.update(any(), any()))
                .thenReturn(new ProductResponse(1L, "Updated Name", "Updated desc", new BigDecimal("19.99")));

        assertThat(mockMvcTester.put().uri("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"name": "Updated Name", "description": "Updated desc", "price": 19.99}
                            """))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                    {"id": 1, "name": "Updated Name", "price": 19.99}
                    """);
    }

    @Test
    void update_returns404_whenProductMissing() {
        when(productService.update(any(), any())).thenThrow(new ProductNotFoundException(99L));

        assertThat(mockMvcTester.put().uri("/api/products/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"name": "Name", "price": 4.50}
                            """))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_returns204_whenSuccessful() {
        assertThat(mockMvcTester.delete().uri("/api/products/1")).hasStatus(HttpStatus.NO_CONTENT);
        verify(productService).delete(1L);
    }
}