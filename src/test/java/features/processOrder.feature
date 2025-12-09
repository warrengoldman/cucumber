Feature: Validating Process Order
@ProcessOrder
  Scenario Outline: Verify if Order is being processed
    Given user places order with "<first name>" "<last name>" <quantity> <price>
    When user wants to process order
    Then the call was successful
    And "qty" in response body is present
    And "qty" integer in response body is <quantity>
  Examples:
    |first name|last name|quantity|price|
    |Jean      |Carmen   |33|32.33|
    |Dave      |Johnson  |1|47.33|