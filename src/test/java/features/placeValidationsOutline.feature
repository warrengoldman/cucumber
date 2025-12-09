# Created by TEST at 11/12/2025
Feature: Validating Apis passing in data via outline

@AddProductOrder
Scenario Outline: # Verify adding product passing order data
  Given Add Place Payload Given Data with "<first name>" "<last name>" <quantity> <price>
  When user calls "PostProduct" with "Post" http request
  Then the API call got success with status code 200
  And "key" in response body is present
  And "body.price" in response body is <price>
#  compare as string too
  And "body.price" in response body is "<price>"
#  sometimes this could be a different scenario, but I think it applies here
  When user calls "GetProductByKey" with "Get" http request with "key" in path
  Given the following orders exist:
    |first name|last name|quantity|price|
    |Jane      |Santorini   |133      |2.33|
    |Hanna      |Beckett  |12       |35.35|
Examples:
  |first name|last name|quantity|price|
  |Jean      |Carmen   |33      |32.33|
  |Dave      |Johnson  |1       |47.33|
