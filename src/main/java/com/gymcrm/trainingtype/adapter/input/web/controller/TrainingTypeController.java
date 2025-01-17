package com.gymcrm.trainingtype.adapter.input.web.controller;

import com.gymcrm.configuration.security.Authenticated;
import com.gymcrm.configuration.security.RequiresPermission;
import com.gymcrm.trainingtype.adapter.input.web.response.TrainingTypeResponse;
import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import io.swagger.annotations.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Training Type Management")
public class TrainingTypeController {
  private final LoadTrainingTypeUseCase loadTrainingTypeUseCase;

  public TrainingTypeController(LoadTrainingTypeUseCase loadTrainingTypeUseCase) {
    this.loadTrainingTypeUseCase = loadTrainingTypeUseCase;
  }

  @GetMapping("/training-types")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINING_TYPES"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Retrieve all training types",
      notes = "Returns a list of all available training types.")
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
    @ApiResponse(code = 200, message = "Successfully retrieved training types."),
    @ApiResponse(code = 401, message = "Unauthorized access."),
    @ApiResponse(
        code = 403,
        message = "Forbidden. You do not have permission to view training types.")
  })
  public List<TrainingTypeResponse> getTrainingTypes() {
    return TrainingTypeResponse.from(loadTrainingTypeUseCase.loadAll());
  }
}
