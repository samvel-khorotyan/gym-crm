package com.gymcrm.cucumber.component.steps;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.cucumber.config.CucumberSpringConfiguration;
import com.gymcrm.user.adapter.input.web.request.LoginRequest;
import com.gymcrm.user.adapter.input.web.request.LoginUpdateRequest;
import com.gymcrm.user.adapter.output.persistence.UserRepository;
import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import com.gymcrm.util.JwtUtil;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class AuthenticationStepsTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TokenBlacklistPort tokenBlacklistPort;

	private ResultActions resultActions;
	private String authToken;

	private final UUID id = UUID.randomUUID();
	private final String firstName = "some-first-name";
	private final String lastName = "some-last-name";
	private final UserType userType = UserType.ADMIN;

	private final User user = User.builder().id(id).firstName(firstName).lastName(lastName).isActive(true)
	        .userType(userType).build();

	@After
	public void cleanupDatabase() {
		try {
			entityManager.createQuery("DELETE FROM Training").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
			entityManager.createQuery("DELETE FROM User").executeUpdate();
			entityManager.flush();
			entityManager.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Given("a user exists with username {string} and password {string}")
	public void aUserExistsWithUsernameAndPassword(String username, String password) {
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		userRepository.save(user);
	}

	@When("the user attempts to login with username {string} and password {string}")
	public void theUserAttemptsToLoginWithUsernameAndPassword(String username, String password) throws Exception {
		LoginRequest loginRequest = new LoginRequest(username, password);
		resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users/me/login")
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)));
	}

	@When("the user attempts to login with username {string} and password {string} {int} times")
	public void theUserAttemptsToLoginWithUsernameAndPasswordMultipleTimes(String username, String password, int times)
	        throws Exception {
		for (int i = 0; i < times; i++) {
			LoginRequest loginRequest = new LoginRequest(username, password);
			mockMvc.perform(MockMvcRequestBuilders.post("/users/me/login").contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(loginRequest)));
		}
	}

	@Then("the login should be successful")
	public void theLoginShouldBeSuccessful() throws Exception {
		resultActions.andExpect(status().isOk());
	}

	@And("a valid JWT token should be returned")
	public void aValidJWTTokenShouldBeReturned() throws Exception {
		MvcResult result = resultActions.andExpect(jsonPath("$.token").exists()).andReturn();
		String responseContent = result.getResponse().getContentAsString();

		Map<String, String> responseMap = objectMapper.readValue(responseContent, new TypeReference<>() {
		});

		authToken = responseMap.get("token");
		Assertions.assertNotNull(authToken);
	}

	@Then("the login should fail with status code {int}")
	public void theLoginShouldFailWithStatusCode(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
	}

	@And("the response should contain error message {string}")
	public void theResponseShouldContainErrorMessage(String errorMessage) throws Exception {
		resultActions.andExpect(jsonPath("$.message", containsString(errorMessage)));
	}

	@Given("the user is authenticated with username {string}")
	public void theUserIsAuthenticatedWithUsername(String username) throws Exception {
		authToken = jwtUtil.generateToken(username, "ROLE_USER");
	}

	@When("the user attempts to change password from {string} to {string}")
	public void theUserAttemptsToChangePasswordFromTo(String oldPassword, String newPassword) throws Exception {
		LoginUpdateRequest updateRequest = new LoginUpdateRequest();
		updateRequest.setUsername(user.getUsername());
		updateRequest.setOldPassword(oldPassword);
		updateRequest.setNewPassword(newPassword);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication")
		        .header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(updateRequest)));
	}

	@Then("the password change should be successful")
	public void thePasswordChangeShouldBeSuccessful() throws Exception {
		resultActions.andExpect(status().isOk());
	}

	@Then("the password change should fail with status code {int}")
	public void thePasswordChangeShouldFailWithStatusCode(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
	}

	@When("the user attempts to logout")
	public void theUserAttemptsToLogout() throws Exception {
		resultActions = mockMvc.perform(
		        MockMvcRequestBuilders.post("/users/me/logout").header("Authorization", "Bearer " + authToken));
	}

	@Then("the logout should be successful")
	public void theLogoutShouldBeSuccessful() throws Exception {
		resultActions.andExpect(status().isOk());
	}

	@And("the token should be blacklisted")
	public void theTokenShouldBeBlacklisted() {
		assertTrue(tokenBlacklistPort.isTokenBlacklisted(authToken));
	}
}
