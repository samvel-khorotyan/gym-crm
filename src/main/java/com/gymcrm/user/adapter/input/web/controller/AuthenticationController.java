package com.gymcrm.user.adapter.input.web.controller;

import com.gymcrm.user.adapter.input.web.request.LoginUpdateRequest;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.UserUpdateUseCase;
import io.swagger.annotations.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Authentication Management")
public class AuthenticationController {
  private final AuthenticationUseCase authenticationUseCase;
  private final UserUpdateUseCase userUpdateUseCase;

  public AuthenticationController(
      AuthenticationUseCase authenticationUseCase, UserUpdateUseCase userUpdateUseCase) {
    this.authenticationUseCase = authenticationUseCase;
    this.userUpdateUseCase = userUpdateUseCase;
  }

  @GetMapping("/users/me/login")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "User Login", notes = "Endpoint for user authentication")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Successfully authenticated"),
    @ApiResponse(code = 401, message = "Unauthorized access - invalid credentials"),
    @ApiResponse(code = 400, message = "Bad Request - Missing or invalid authentication headers")
  })
  public void login(
      @ApiParam(value = "User's username", required = true)
          @RequestHeader("username")
          @NotBlank(message = "Username cannot be blank")
          String username,
      @ApiParam(value = "User's password", required = true)
          @RequestHeader("password")
          @NotBlank(message = "Password cannot be blank")
          String password) {

    authenticationUseCase.authenticate(username, password);
  }

  @PutMapping("/users/me/login")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Update Login Details",
      notes = "Allows users to update their login credentials, such as passwords.")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Login details updated successfully"),
    @ApiResponse(code = 400, message = "Invalid request data"),
    @ApiResponse(code = 401, message = "Unauthorized access - invalid credentials")
  })
  public void updateLoginDetails(
      @ApiParam(value = "Request body containing the updated login details", required = true)
          @RequestBody
          @Valid
          LoginUpdateRequest request) {

    userUpdateUseCase.updatePassword(request.toCommand());
  }
}
