package com.gymcrm.user.adapter.input.web.controller;

import com.gymcrm.user.adapter.input.web.request.LoginRequest;
import com.gymcrm.user.adapter.input.web.request.LoginUpdateRequest;
import com.gymcrm.user.adapter.input.web.response.AuthResponse;
import com.gymcrm.user.application.port.input.BlacklistTokenUseCase;
import com.gymcrm.user.application.port.input.UserUpdateUseCase;
import io.swagger.annotations.*;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Authentication Management")
public class AuthenticationController {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	private final UserUpdateUseCase userUpdateUseCase;
	private final BlacklistTokenUseCase blacklistTokenUseCase;

	public AuthenticationController(UserUpdateUseCase userUpdateUseCase, BlacklistTokenUseCase blacklistTokenUseCase) {
		this.userUpdateUseCase = userUpdateUseCase;
		this.blacklistTokenUseCase = blacklistTokenUseCase;
	}

	@PostMapping("/users/me/login")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "User Login",notes = "Endpoint for user authentication")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully authenticated",response = AuthResponse.class),
	        @ApiResponse(code = 401,message = "Unauthorized access - invalid credentials"),
	        @ApiResponse(code = 400,message = "Bad Request - Missing or invalid authentication fields")})
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(AuthResponse.form("example_jwt_token"));
	}

	@PutMapping("/users/me/authentication")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Update Login Details",notes = "Allows users to update their login credentials, such as passwords.")
	@ApiResponses({@ApiResponse(code = 200,message = "Login details updated successfully"),
	        @ApiResponse(code = 400,message = "Invalid request data"),
	        @ApiResponse(code = 401,message = "Unauthorized access - invalid credentials")})
	public void updateLoginDetails(
	        @ApiParam(value = "Request body containing the updated login details",required = true) @Valid @RequestBody LoginUpdateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received request to update login details for user: {}", transactionId,
		        request.getUsername());

		userUpdateUseCase.updatePassword(request.toCommand());

		logger.info("Transaction ID: {} - Successfully updated login details for user: {}", transactionId,
		        request.getUsername());

		MDC.clear();
	}

	@PostMapping("/users/me/logout")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Logs out the user",notes = "Invalidates the current JWT token")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully logged out"),
	        @ApiResponse(code = 400,message = "Invalid token"), @ApiResponse(code = 401,message = "Unauthorized")})
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		if (token.startsWith("Bearer "))
			token = token.substring(7);

		blacklistTokenUseCase.blacklistToken(token, 60 * 60 * 1000);

		return ResponseEntity.ok("Logged out successfully.");
	}
}
