package stepDefinitions;

import com.rest.cucumber.model.TestOrderFactory;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class StepDefinition {
    private static final String LOG_FILE = "log.txt";
    private static final String REQUEST_LOG_FILE = LOG_FILE;
    private static final String RESPONSE_LOG_FILE = LOG_FILE;
    private RequestSpecification spec = RequestSpec.getSpec(REQUEST_LOG_FILE, RESPONSE_LOG_FILE);
    private ValidatableResponse response;
    @Given("Add Place Payload")
    public void add_place_payload() {
        spec.body(TestOrderFactory.createOrder());
    }
    @Given("Add Place Payload Given Data with {string} {string} {int} {double}")
    public void add_place_payload_given_data_with(String firstName, String lastName, int quantity, double price) {
        spec.body(TestOrderFactory.createOrder(firstName, lastName, quantity, price));
    }
    @When("user calls {string} with {string} http request")
    public void user_calls_with_http_request(String uriEnumName, String httpMethod) {
        user_calls_with_http_request_with_in_path(uriEnumName, httpMethod, null);
    }
    @When("user calls {string} with {string} http request with {string} in path")
    public void user_calls_with_http_request_with_in_path(String uriEnumName, String httpMethod, String pathVarName) {
        String uri = APIResources.valueOf(uriEnumName).getUri();
        if (pathVarName != null && !pathVarName.isEmpty()) {
            String valInPrevResponseForPathKey = response.extract().jsonPath().getString(pathVarName);
            spec.pathParam(pathVarName, valInPrevResponseForPathKey);
        }
        if (httpMethod.equalsIgnoreCase("Get")) {
            response = spec.get(uri).then();
        } else {
            //TODO all other httpMethods (like DELETE, PUT, PATCH) need adding
            response = spec.post(uri).then();
        }
    }
    @Then("the API call got success with status code {int}")
    public void the_api_call_got_success_with_status_code(Integer expectedStatusCode) {
        response.statusCode(expectedStatusCode);
    }
    @Then("{string} in response body is {string}")
    public void in_response_body_is(String key, String expectedValue) {
        // instead of overloaded below for float (and others needed)
        // we could turn value in response to string and compare
        // i.e.
        String actualValue = response.extract().jsonPath().getString(key);
        assertEquals(actualValue, expectedValue);
        //response.body(key, equalTo(expectedValue));
    }
    // below method is required if users code gherkin without quote on data from examples
    // And "body.price" in response body is <price>
    @Then("{string} in response body is {float}")
    public void in_response_body_is(String key, Float expectedValue) {
        response.body(key, equalTo(expectedValue));
    }
    @Then("{string} in response body is present")
    public void in_response_body_is_present(String key) {
        response.body(key, is(notNullValue()));
    }
    @BeforeAll
    public static void cleanUpBeforeAllTests() {
        try {
            Files.delete(Paths.get(REQUEST_LOG_FILE));
        } catch (NoSuchFileException ignore) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            Files.delete(Paths.get(RESPONSE_LOG_FILE));
        } catch (NoSuchFileException ignore) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}