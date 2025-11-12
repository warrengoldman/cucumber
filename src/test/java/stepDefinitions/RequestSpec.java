package stepDefinitions;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;

public class RequestSpec {
    private static final String BASE_URI = "http://localhost:8080/home";

    public static RequestSpecification getSpec(String requestLog, String responseLog) {
        return given().spec(new MyRequestSpecBuilder().setBaseUri(BASE_URI)
                        .addFilter(requestLog != null ? RequestLoggingFilter.logRequestTo(getLog(requestLog)) : null)
                        .addFilter(responseLog != null ? ResponseLoggingFilter.logResponseTo(getLog(responseLog)) : null)
                        .build())
                .header("Content-Type", "application/json");
    }

    private static class MyRequestSpecBuilder extends RequestSpecBuilder {
        public RequestSpecBuilder addFilter(io.restassured.filter.Filter filter) {
            if (filter != null) {
                return super.addFilter(filter);
            }
            return this;
        }
    }
    private static PrintStream getLog(String logFilename) {
        if (logFilename != null && !logFilename.isEmpty()) {
            try {
                return new PrintStream(new FileOutputStream(logFilename, true));
            } catch (FileNotFoundException specifiedTrueToCreateIfNotExistsExceptionShouldNotHappen) {
                // returning null if it does, but don't think there will be a way to test this
            }
        }
        return null;
    }
}
