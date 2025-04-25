package com.gymcrm.cucumber.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/integration",glue = {
        "com.gymcrm.cucumber.integration.steps"},plugin = {"pretty",
                "html:target/cucumber-reports/cucumber-report.html",
                "json:target/cucumber-reports/cucumber-report.json",
                "junit:target/cucumber-reports/cucumber-report.xml"})
@ActiveProfiles("testing")
public class CucumberIntegrationTest {
}
