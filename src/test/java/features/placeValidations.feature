Feature: Validating Place API's
Scenario: Verify if Place is being successfully added using AddPlaceAPI
  Given Add Place Payload
  When user calls "/post/product" with Post http request
  Then the API call got success with status code 200
  And "key" in response body is valid for "/get/product/{key}" call
  And "path" in response body is "product"
  And "body" in response body is present