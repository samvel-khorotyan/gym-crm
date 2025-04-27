package com.gymcrm.cucumber.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/integration/training_integration.feature",glue = {
        "com.gymcrm.cucumber.integration.steps",
        "com.gymcrm.cucumber.config"},plugin = {"pretty", "json:target/cucumber-json-report/report.json"})
@ActiveProfiles("testing")
public class TrainingIntegrationTest {
}
