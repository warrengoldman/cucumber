package stepDefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.cucumber.model.Book;
import com.rest.cucumber.model.Order;
import com.rest.cucumber.model.TestOrderFactory;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DocStringType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class StepDefinition {
    private static final String LOG_FILE = "log.txt";
    private static final String REQUEST_LOG_FILE = LOG_FILE;
    private static final String RESPONSE_LOG_FILE = LOG_FILE;
    private RequestSpecification spec = RequestSpec.getSpec(REQUEST_LOG_FILE, RESPONSE_LOG_FILE);
    private ValidatableResponse response;
    private Map<String, Object> scenarioContext = new HashMap<>(); // To store variables

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

    @Before
    public void setup(Scenario scenario) {
        scenarioContext.put("scenario", scenario);
    }

    @Given("Add Place Payload")
    public void add_place_payload() {
        spec.body(TestOrderFactory.createOrder());
    }
    @Given("user places order with {string} {string} {int} {double}")
    public void user_places_order(String firstName, String lastName, int quantity, double price) {
        String msg = "User places order with %s %s %d %.2f".formatted(firstName, lastName, quantity, price);
        scenarioContext.put("userPlacesOrderMsg", msg);
        add_place_payload_given_data_with(firstName, lastName, quantity, price);
    }
    @Given("Add Place Payload Given Data with {string} {string} {int} {double}")
    public void add_place_payload_given_data_with(String firstName, String lastName, int quantity, double price) {
        String msg = "Add Place Payload Given Data with %s %s %d %.2f".formatted(firstName, lastName, quantity, price);
        scenarioContext.put("addPlaceMsg", msg);
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
    @Then("the call was successful")
    public void the_call_was_successful() {
        the_api_call_got_success_with_status_code(200);
    }
    @Then("{string} in response body is {string}")
    public void in_response_body_is(String key, String expectedValue) {
        // in case the key in the body is not a string (maybe it is a float)
        // get it as a string so we can compare to expectedValue (which is a string)
        String actualValue = response.extract().jsonPath().getString(key);
        assertEquals(actualValue, expectedValue);
    }
    @Then("{string} in response body is {float}")
    public void in_response_body_is(String key, Float expectedValue) {
        Scenario scenario = (Scenario)scenarioContext.get("scenario");
        System.out.printf("scenario: %s%n userPlacesOrderMsg: %s%n addPlaceMsg: %s%n", getString(scenario), scenarioContext.get("userPlacesOrderMsg"), scenarioContext.get("addPlaceMsg"));
        response.body(key, equalTo(expectedValue));
    }
    private String getString(Scenario scenario) {
        return "Current Scenario Name: %s, Scenario ID: %s, Scenario URI: %s, Scenario Tags: %s, Scenario Line: %s, Scenario Status: %s, Scenario Failed: %b"
                .formatted(scenario.getName(), scenario.getId()
                        , scenario.getUri(), scenario.getSourceTagNames()
                        , scenario.getLine(), scenario.getStatus(), scenario.isFailed());
    }
    @Then("{string} integer in response body is {int}")
    public void in_response_body_is(String key, Integer expectedValue) {
        Scenario scenario = (Scenario)scenarioContext.get("scenario");
        System.out.printf("scenario: %s%n userPlacesOrderMsg: %s%n addPlaceMsg: %s%n", getString(scenario), scenarioContext.get("userPlacesOrderMsg"), scenarioContext.get("addPlaceMsg"));
        response.body(key, equalTo(expectedValue));
    }
    @Then("{string} in response body is present")
    public void in_response_body_is_present(String key) {
        response.body(key, is(notNullValue()));
    }
    @When("user wants to process order")
    public void user_wants_to_process_order() {
        scenarioContext.put("currentInventory", 42);
        user_calls_with_http_request_with_in_path("ProcessOrder", "get", null);
    }

    @Given("log\\({string})")
    public void log(String arg0) {
        // method to allow logging to cucumber test results
        Scenario scenario = (Scenario) scenarioContext.get("scenario");
        scenario.log(arg0);
    }
    // Gherkin of the following, will use the @DataTableType and the
//    Given the following orders ll exist:
//            |first name|last name|quantity|price|
//            |Jane      |Santorini   |133      |2.33|
//            |Hanna      |Beckett  |12       |35.35|
    @DataTableType
    public Order convertToOrder(Map<String, String> entry) {
        String custId = entry.get("first name") + entry.get("last name");
        return new Order(-1, Integer.parseInt(entry.get("quantity")), Double.parseDouble(entry.get("price")), custId);
    }

    @Given("the following orders exist:")
    public void couldCreateOrCheckTheyExistInLogic(List<Order> orders) {
        System.out.println("Following orders exist:" + orders);
    }

    // the name of the method must match the name of the variable in the Given below
    @ParameterType(".*")
    public Book favoriteBookName(String bookName) {
        // would go find other metadata for bookName and value at this point
        return new Book(bookName, null, null);
    }

    @Given("{favoriteBookName} is my favorite book")
    public void this_is_my_favorite_book(Book myBook) {
        System.out.println("Book is my favorite book:"  + myBook);
    }

    @ParameterType(".*")
    public Order orderId(String orderKey) {
        return TestOrderFactory.getOrders().getFirst();
    }

    @Given("Order with id of {orderId} exists")
    public void order_with_id(Order order) {
        System.out.println("Order is:"  + order);
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    @DocStringType
    public JsonNode json(String docString) throws JsonProcessingException {
        return objectMapper.readValue(docString, JsonNode.class);
    }

    @Given("Books are defined by json")
    public void books_are_defined_by_json(JsonNode books) {
        List<Book> bookList = new ArrayList<>();
        if (books.isArray()) {
            books.forEach(book -> bookList.add(new ObjectMapper().convertValue(book, Book.class)));
        }
        System.out.println("Books are:"  + bookList);
    }
}