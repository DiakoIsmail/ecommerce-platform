Feature: Product management
  As an API consumer
  I want to create and retrieve products
  So that the catalog can be managed

  Scenario: Creating a new product
    When I create a product named "Desk Lamp" described as "LED, adjustable" priced at 29.90
    Then the response status should be 201
    And the returned product should have the name "Desk Lamp"
    And the returned product should have the price 29.90

  Scenario: Fetching a product that does not exist
    When I request the product with id 999999
    Then the response status should be 404