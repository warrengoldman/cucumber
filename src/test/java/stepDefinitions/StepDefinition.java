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
    @When("user calls {string} with Post http request")
    public void user_calls_with_post_http_request(String postUri) {
        response = spec.post(postUri).then();
    }
    @Then("the API call got success with status code {int}")
    public void the_api_call_got_success_with_status_code(Integer expectedStatusCode) {
        response.statusCode(expectedStatusCode);
    }
    @Then("{string} in response body is {string}")
    public void in_response_body_is(String key, String expectedValue) {
        response.body(key, equalTo(expectedValue));
    }
    @Then("{string} in response body is present")
    public void in_response_body_is_present(String key) {
        response.body(key, is(notNullValue()));
    }
    @Then("{string} in response body is valid for {string} call")
    public void in_response_body_is_valid_for_call(String key, String uri) {
        String id = response.extract().jsonPath().getString(key);
        RequestSpec.getSpec(REQUEST_LOG_FILE, RESPONSE_LOG_FILE).pathParam("key", id).get(uri).then().body( key, is(notNullValue()));
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