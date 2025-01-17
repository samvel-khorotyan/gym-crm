package com.gymcrm.training.adapter.input.web.controller;

import com.gymcrm.configuration.security.Authenticated;
import com.gymcrm.configuration.security.RequiresPermission;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import io.swagger.annotations.*;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Training Management")
public class TrainingController {
  private final TrainingCreationUseCase trainingCreationUseCase;

  public TrainingController(TrainingCreationUseCase trainingCreationUseCase) {
    this.trainingCreationUseCase = trainingCreationUseCase;
  }

  @PostMapping("/trainings")
  @Authenticated
  @RequiresPermission({"CREATE_TRAINING"})
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(
      value = "Create a new training",
      notes = "Creates a new training session based on the provided details.")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "authUsername",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataType = "string"),
    @ApiImplicitParam(
        name = "authPassword",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataType = "string")
  })
  @ApiResponses({
    @ApiResponse(code = 201, message = "Training created successfully."),
    @ApiResponse(code = 400, message = "Invalid input data provided.")
  })
  public void create(
      @ApiParam(value = "Details required to create a training session", required = true)
          @RequestBody
          @Valid
          TrainingCreateRequest request) {
    trainingCreationUseCase.create(request.toCommand());
  }
}
