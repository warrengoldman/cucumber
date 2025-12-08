package cucumber.Options;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/features", // Path to your feature files
        glue = {"stepDefinitions"}, // Package(s) containing your step definition
//        to run a specific test
//        tags = "@ProcessOrder",
        plugin = {"pretty", "html:target/cucumber/cucumber.html"}
)
public class TestRunner {
}
