Feature: Validating Place API's
@ProductOrderStaticOrders
Scenario: Verify if Place is being successfully added using AddPlaceAPI
  Given Add Place Payload
  When user calls "PostProduct" with "Post" http request
  Then the API call got success with status code 200
  And "key" in response body is present
  When user calls "GetProductByKey" with "Get" http request with "key" in path
