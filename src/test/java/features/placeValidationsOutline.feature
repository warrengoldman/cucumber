# Created by TEST at 11/12/2025
Feature: Validating Apis passing in data via outline

Scenario Outline: # Verify adding product passing order data
  Given Add Place Payload Given Data with "<first name>" "<last name>" <quantity> <price>
  When user calls "PostProduct" with Post http request
  Then the API call got success with status code 200
  And "key" in response body is valid for "GetProductByKey" call
  And "path" in response body is "product"
  And "body" in response body is present
Examples:
  |first name|last name|quantity|price|
  |Jean      |Carmen   |33      |32.33|