package com.gymcrm.cucumber.integration.steps;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.cucumber.config.CucumberSpringConfiguration;
import com.gymcrm.user.adapter.input.web.request.LoginRequest;
import com.gymcrm.user.adapter.input.web.request.LoginUpdateRequest;
import com.gymcrm.user.application.UserService;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import com.gymcrm.util.JwtUtil;
import com.gymcrm.util.PasswordStorage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class AuthenticationIntegrationSteps {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationIntegrationSteps.class);

	private static final String LOGIN_ENDPOINT = "/users/me/login";
	private static final String LOGOUT_ENDPOINT = "/users/me/logout";
	private static final String UPDATE_AUTH_ENDPOINT = "/users/me/authentication";
	private static final String PROTECTED_RESOURCE_ENDPOINT = "/api/protected-resource";
	private static final String TEST_USER_PLACEHOLDER = "test.user";
	private static final String TEST_USERNAME_PLACEHOLDER = "testuser";
	private static final String DEFAULT_PASSWORD = "Password123";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TokenBlacklistPort tokenBlacklistPort;

	private ResultActions resultActions;
	private String authToken;
	private String generatedUsername;
	private String generatedPassword;

	@Before
	public void setup() {
		authToken = null;
		generatedUsername = null;
		generatedPassword = null;
	}

	@Given("the system has a user with the following details:")
	public void theSystemHasAUserWithTheFollowingDetails(DataTable dataTable) {
		Map<String, String> userData = dataTable.asMaps().get(0);
		createUserFromData(userData);
	}

	@When("the client sends a POST request to {string} with:")
	public void theClientSendsAPOSTRequestToWith(String endpoint, DataTable dataTable) throws Exception {
		Map<String, String> requestData = dataTable.asMap();

		if (LOGIN_ENDPOINT.equals(endpoint)) {
			performLogin(requestData);
		} else if (LOGOUT_ENDPOINT.equals(endpoint)) {
			performLogout();
		}
	}

	@When("the client sends a POST request to {string}")
	public void theClientSendsAPOSTRequestTo(String endpoint) throws Exception {
		resultActions = mockMvc
		        .perform(MockMvcRequestBuilders.post(endpoint).header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken));
		logger.info("Sent POST request to {}", endpoint);
	}

	@When("the client sends a PUT request to {string} with:")
	public void theClientSendsAPUTRequestToWith(String endpoint, DataTable dataTable) throws Exception {
		Map<String, String> requestData = dataTable.asMap();

		if (UPDATE_AUTH_ENDPOINT.equals(endpoint)) {
			updateAuthentication(requestData);
		}
	}

	@Then("the response status code should be {int}")
	public void theResponseStatusCodeShouldBe(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
		logger.info("Verified response status code: {}", statusCode);
	}

	@And("the response should contain a valid JWT token")
	public void theResponseShouldContainAValidJWTToken() throws Exception {
		resultActions.andExpect(jsonPath("$.token").exists());

		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseContent, new TypeReference<>() {
		});
		authToken = responseMap.get("token");
		logger.info("Extracted JWT token from response");
	}

	@And("the response should contain an error message {string}")
	public void theResponseShouldContainAnErrorMessage(String errorMessage) throws Exception {
		resultActions.andExpect(jsonPath("$.message", containsString(errorMessage)));
		logger.info("Verified error message: {}", errorMessage);
	}

	@Given("the client is authenticated with username {string}")
	public void theClientIsAuthenticatedWithUsername(String username) {
		if (TEST_USERNAME_PLACEHOLDER.equals(username)) {
			username = generatedUsername;
		}
		authToken = jwtUtil.generateToken(username, "ROLE_TRAINEE");
		logger.info("Client authenticated with username: {}", username);
	}

	@And("the token should be blacklisted")
	public void theTokenShouldBeBlacklisted() {
		assertTrue(tokenBlacklistPort.isTokenBlacklisted(authToken), "Token should be blacklisted");
		logger.info("Verified token is blacklisted");
	}

	@And("the client has logged out")
	public void theClientHasLoggedOut() throws Exception {
		mockMvc.perform(
		        MockMvcRequestBuilders.post(LOGOUT_ENDPOINT).header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken));
		logger.info("Client logged out");
	}

	@When("the client tries to access a protected resource")
	public void theClientTriesToAccessAProtectedResource() throws Exception {
		resultActions = mockMvc.perform(MockMvcRequestBuilders.get(PROTECTED_RESOURCE_ENDPOINT)
		        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken));
		logger.info("Client attempted to access protected resource");
	}

	private void createUserFromData(Map<String, String> userData) {
		CreateUserCommand command = CreateUserCommand.builder().firstName(userData.get("firstName"))
		        .lastName(userData.get("lastName")).password("dummy")
		        .userType(UserType.valueOf(userData.get("userType"))).build();

		User user = userService.create(command);

		generatedUsername = user.getUsername();
		UUID userId = user.getId();
		generatedPassword = PasswordStorage.getPassword(userId);

		logger.info("Created user with username: {}", generatedUsername);
	}

	private void performLogin(Map<String, String> requestData) throws Exception {
		String username = requestData.get("username");
		String password = requestData.get("password");

		if (TEST_USER_PLACEHOLDER.equals(username)) {
			username = generatedUsername;
		}

		if (DEFAULT_PASSWORD.equals(password) && username.equals(generatedUsername)) {
			password = generatedPassword;
		}

		LoginRequest loginRequest = new LoginRequest(username, password);
		logger.info("Attempting login with username: {}", username);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_ENDPOINT)
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)));

		logger.debug("Login response: {}", resultActions.andReturn().getResponse().getContentAsString());
	}

	private void performLogout() throws Exception {
		resultActions = mockMvc.perform(
		        MockMvcRequestBuilders.post(LOGOUT_ENDPOINT).header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken));
		logger.info("Performed logout request");
	}

	private void updateAuthentication(Map<String, String> requestData) throws Exception {
		String username = requestData.get("username");
		String oldPassword = requestData.get("oldPassword");
		String newPassword = requestData.get("newPassword");

		if (TEST_USERNAME_PLACEHOLDER.equals(username)) {
			username = generatedUsername;
		}

		if (DEFAULT_PASSWORD.equals(oldPassword)) {
			oldPassword = generatedPassword;
		}

		LoginUpdateRequest updateRequest = new LoginUpdateRequest(username, oldPassword, newPassword);
		logger.info("Updating authentication for username: {}", username);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_AUTH_ENDPOINT)
		        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(updateRequest)));
	}
}
