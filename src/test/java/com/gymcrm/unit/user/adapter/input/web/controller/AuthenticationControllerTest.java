package com.gymcrm.unit.user.adapter.input.web.controller;

import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.user.adapter.input.web.controller.AuthenticationController;
import com.gymcrm.user.application.port.input.BlacklistTokenUseCase;
import com.gymcrm.user.application.port.input.UpdatePasswordCommand;
import com.gymcrm.user.application.port.input.UserUpdateUseCase;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthenticationControllerTest {
	private MockMvc mockMvc;

	private BlacklistTokenUseCase blacklistTokenUseCase;
	private UserUpdateUseCase userUpdateUseCase;

	private ObjectMapper objectMapper;

	private String username;
	private String oldPassword;
	private String newPassword;

	@BeforeEach
	public void setup() {
		blacklistTokenUseCase = Mockito.mock(BlacklistTokenUseCase.class);
		userUpdateUseCase = Mockito.mock(UserUpdateUseCase.class);

		AuthenticationController authenticationController = new AuthenticationController(userUpdateUseCase,
		        blacklistTokenUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

		objectMapper = new ObjectMapper();

		username = "john.doe";
		oldPassword = "password123";
		newPassword = "password456";
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenUsernameIsEmpty() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("username", "");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenUsernameIsTooLong() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("username",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint.It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenOldPasswordIsEmpty() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("old_password", "");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenOldPasswordExceedsMaxLength() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("old_password",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint.It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenNewPasswordIsEmpty() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("new_password", "");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateLoginDetails_ShouldReturnBadRequest_WhenNewPasswordExceedsMaxLength() throws Exception {
		Map<String, Object> traineeRequest = loginUpdateRequest();
		traineeRequest.put("new_password",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint.It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updatePassword_ShouldReturnOk_WhenRequestIsValid() throws Exception {
		doNothing().when(userUpdateUseCase).updatePassword(getUpdatePasswordCommand());

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/authentication").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(loginUpdateRequest())))
		        .andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void logout_ShouldReturnOk() throws Exception {
		String token = "some-token";

		doNothing().when(blacklistTokenUseCase).blacklistToken(token, 60 * 60 * 1000);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/logout").header("Authorization", "Bearer " + token)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.content().string("Logged out successfully."));
	}

	private UpdatePasswordCommand getUpdatePasswordCommand() {
		return new UpdatePasswordCommand(username, oldPassword, newPassword);
	}

	public Map<String, Object> loginUpdateRequest() {
		Map<String, Object> map = new HashMap<>();
		map.put("username", username);
		map.put("old_password", oldPassword);
		map.put("new_password", newPassword);
		return map;
	}
}
