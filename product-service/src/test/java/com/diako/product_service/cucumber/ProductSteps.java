package com.diako.product_service.cucumber;

import com.diako.product_service.dto.ProductRequest;
import com.diako.product_service.dto.ProductResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<ProductResponse> response;

    @When("I create a product named {string} described as {string} priced at {double}")
    public void iCreateAProduct(String name, String description, double price) {
        ProductRequest request = new ProductRequest(name, description, BigDecimal.valueOf(price));
        response = restTemplate.postForEntity("/api/products", request, ProductResponse.class);
    }

    @When("I request the product with id {long}")
    public void iRequestTheProductWithId(long id) {
        response = restTemplate.getForEntity("/api/products/" + id, ProductResponse.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @And("the returned product should have the name {string}")
    public void theReturnedProductShouldHaveTheName(String expectedName) {
        assertThat(response.getBody().name()).isEqualTo(expectedName);
    }

    @And("the returned product should have the price {double}")
    public void theReturnedProductShouldHaveThePrice(double expectedPrice) {
        assertThat(response.getBody().price()).isEqualByComparingTo(BigDecimal.valueOf(expectedPrice));
    }
}