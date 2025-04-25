package com.gymcrm.cucumber.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/component/authentication.feature",glue = {
        "com.gymcrm.cucumber.component.steps",
        "com.gymcrm.cucumber.config"},plugin = {"pretty", "json:target/cucumber-json-report/report.json"})
public class AuthenticationTest {
}
