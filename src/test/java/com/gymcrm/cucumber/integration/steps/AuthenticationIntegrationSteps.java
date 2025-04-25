package com.gymcrm.cucumber.integration.steps;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
@CucumberContextConfiguration
public class AuthenticationIntegrationSteps {
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

	@Given("the system has a user with the following details:")
	public void theSystemHasAUserWithTheFollowingDetails(DataTable dataTable) {
		Map<String, String> userData = dataTable.asMaps().get(0);

		CreateUserCommand command = CreateUserCommand.builder().firstName(userData.get("firstName"))
		        .lastName(userData.get("lastName")).password("dummy")
		        .userType(UserType.valueOf(userData.get("userType"))).build();

		User user = userService.create(command);

		generatedUsername = user.getUsername();
		UUID userId = user.getId();

		generatedPassword = PasswordStorage.getPassword(userId);
	}

	@When("the client sends a POST request to {string} with:")
	public void theClientSendsAPOSTRequestToWith(String endpoint, DataTable dataTable) throws Exception {
		Map<String, String> requestData = dataTable.asMap();

		if (endpoint.equals("/users/me/login")) {
			String username = requestData.get("username");
			if (username.equals("test.user")) {
				username = generatedUsername;
			}

			String password = requestData.get("password");
			if (password.equals("Password123") && username.equals(generatedUsername)) {
				password = generatedPassword;
			}

			LoginRequest loginRequest = new LoginRequest(username, password);

			System.out.println("Attempting login with username: " + username + " and password: " + password);

			resultActions = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
			        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)));

			System.out.println("Login response: " + resultActions.andReturn().getResponse().getContentAsString());
		} else if (endpoint.equals("/users/me/logout")) {
			resultActions = mockMvc
			        .perform(MockMvcRequestBuilders.post(endpoint).header("Authorization", "Bearer " + authToken));
		}
	}

	@When("the client sends a POST request to {string}")
	public void theClientSendsAPOSTRequestTo(String endpoint) throws Exception {
		resultActions = mockMvc
		        .perform(MockMvcRequestBuilders.post(endpoint).header("Authorization", "Bearer " + authToken));
	}

	@When("the client sends a PUT request to {string} with:")
	public void theClientSendsAPUTRequestToWith(String endpoint, DataTable dataTable) throws Exception {
		Map<String, String> requestData = dataTable.asMap();

		if (endpoint.equals("/users/me/authentication")) {
			String username = requestData.get("username");
			if (username.equals("testuser")) {
				username = generatedUsername;
			}

			String oldPassword = requestData.get("oldPassword");
			if (oldPassword.equals("Password123")) {
				oldPassword = generatedPassword;
			}

			LoginUpdateRequest updateRequest = new LoginUpdateRequest(username, oldPassword,
			        requestData.get("newPassword"));

			resultActions = mockMvc.perform(MockMvcRequestBuilders.put(endpoint)
			        .header("Authorization", "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(updateRequest)));
		}
	}

	@Then("the response status code should be {int}")
	public void theResponseStatusCodeShouldBe(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
	}

	@And("the response should contain a valid JWT token")
	public void theResponseShouldContainAValidJWTToken() throws Exception {
		resultActions.andExpect(jsonPath("$.token").exists());

		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		Map<String, String> responseMap = objectMapper.readValue(responseContent, new TypeReference<>() {
		});
		authToken = responseMap.get("token");
	}

	@And("the response should contain an error message {string}")
	public void theResponseShouldContainAnErrorMessage(String errorMessage) throws Exception {
		resultActions.andExpect(jsonPath("$.message", containsString(errorMessage)));
	}

	@Given("the client is authenticated with username {string}")
	public void theClientIsAuthenticatedWithUsername(String username) {
		if (username.equals("testuser")) {
			username = generatedUsername;
		}
		authToken = jwtUtil.generateToken(username, "ROLE_TRAINEE");
	}

	@And("the token should be blacklisted")
	public void theTokenShouldBeBlacklisted() {
		assertTrue(tokenBlacklistPort.isTokenBlacklisted(authToken));
	}

	@And("the client has logged out")
	public void theClientHasLoggedOut() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/logout").header("Authorization", "Bearer " + authToken));
	}

	@When("the client tries to access a protected resource")
	public void theClientTriesToAccessAProtectedResource() throws Exception {
		resultActions = mockMvc.perform(
		        MockMvcRequestBuilders.get("/api/protected-resource").header("Authorization", "Bearer " + authToken));
	}
}
