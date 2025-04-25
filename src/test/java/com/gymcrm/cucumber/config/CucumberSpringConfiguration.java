package com.gymcrm.cucumber.config;

import com.gymcrm.GymCRMApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@ActiveProfiles("testing")
@CucumberContextConfiguration
@SpringBootTest(classes = GymCRMApplication.class)
public class CucumberSpringConfiguration {
}
