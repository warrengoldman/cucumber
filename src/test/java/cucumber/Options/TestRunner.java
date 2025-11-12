package cucumber.Options;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/features", // Path to your feature files
        glue = {"stepDefinitions"}, // Package(s) containing your step definitions
        plugin = {"pretty", "html:target/cucumber/cucumber.html"}
)
public class TestRunner {
}
