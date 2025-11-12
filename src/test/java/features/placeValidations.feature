Feature: Validating Place API's
Scenario: Verify if Place is being successfully added using AddPlaceAPI
  Given Add Place Payload
  When user calls "PostProduct" with Post http request
  Then the API call got success with status code 200
  And "key" in response body is valid for "GetProductByKey" call
  And "path" in response body is "product"
  And "body" in response body is present