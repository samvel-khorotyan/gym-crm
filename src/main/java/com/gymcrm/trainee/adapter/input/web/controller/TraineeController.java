package com.gymcrm.trainee.adapter.input.web.controller;

import com.gymcrm.configuration.security.Authenticated;
import com.gymcrm.configuration.security.RequiresPermission;
import com.gymcrm.trainee.adapter.input.web.request.TraineeActivateDeactivateRequest;
import com.gymcrm.trainee.adapter.input.web.request.TraineeCreateRequest;
import com.gymcrm.trainee.adapter.input.web.request.TraineeTrainersUpdateRequest;
import com.gymcrm.trainee.adapter.input.web.request.TraineeUpdateRequest;
import com.gymcrm.trainee.adapter.input.web.response.TraineeLightResponse;
import com.gymcrm.trainee.adapter.input.web.response.TraineeResponse;
import com.gymcrm.trainee.adapter.input.web.response.TraineeTrainingsResponse;
import com.gymcrm.trainee.application.port.input.LoadTraineeUseCase;
import com.gymcrm.trainee.application.port.input.TraineeCreationUseCase;
import com.gymcrm.trainee.application.port.input.TraineeUpdateUseCase;
import com.gymcrm.trainer.adapter.input.web.response.TrainerUserDetailsResponse;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import io.swagger.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Trainee Management")
public class TraineeController {
  private final TraineeCreationUseCase traineeCreationUseCase;
  private final TraineeUpdateUseCase traineeUpdateUseCase;
  private final LoadTrainingUseCase loadTrainingUseCase;
  private final LoadTraineeUseCase loadTraineeUseCase;

  public TraineeController(
      TraineeCreationUseCase traineeCreationUseCase,
      TraineeUpdateUseCase traineeUpdateUseCase,
      LoadTrainingUseCase loadTrainingUseCase,
      LoadTraineeUseCase loadTraineeUseCase) {
    this.traineeCreationUseCase = traineeCreationUseCase;
    this.traineeUpdateUseCase = traineeUpdateUseCase;
    this.loadTrainingUseCase = loadTrainingUseCase;
    this.loadTraineeUseCase = loadTraineeUseCase;
  }

  @PostMapping("/trainees")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create Trainee", notes = "Registers a new trainee in the system")
  @ApiResponses({
    @ApiResponse(code = 201, message = "Trainee created successfully"),
    @ApiResponse(code = 400, message = "Invalid input data")
  })
  public TraineeLightResponse create(
      @ApiParam(value = "Trainee creation details", required = true) @RequestBody @Valid
          TraineeCreateRequest request) {
    return TraineeLightResponse.from(traineeCreationUseCase.create(request.toCommand()));
  }

  @GetMapping("/trainees")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINEES"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get Trainee", notes = "Fetches trainee details by username")
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
    @ApiResponse(code = 200, message = "Trainee details fetched"),
    @ApiResponse(code = 404, message = "Trainee not found"),
    @ApiResponse(code = 401, message = "Unauthorized access")
  })
  public TraineeResponse getByUsername(
      @ApiParam(value = "Trainee username to fetch details", required = true)
          @RequestParam("username")
          String username) {

    return TraineeResponse.from(loadTraineeUseCase.loadByUsername(username));
  }

  @PutMapping("/trainees/{traineeId}")
  @Authenticated
  @RequiresPermission({"UPDATE_TRAINEES"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Update Trainee", notes = "Updates an existing trainee's details")
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
    @ApiResponse(code = 200, message = "Trainee updated successfully"),
    @ApiResponse(code = 400, message = "Invalid data provided"),
    @ApiResponse(code = 404, message = "Trainee not found")
  })
  public TraineeResponse update(
      @ApiParam(value = "Trainee ID", required = true) @PathVariable UUID traineeId,
      @ApiParam(value = "Trainee update details", required = true) @RequestBody @Valid
          TraineeUpdateRequest request) {
    return TraineeResponse.from(traineeUpdateUseCase.update(request.toCommand(traineeId)));
  }

  @DeleteMapping("/trainees")
  @Authenticated
  @RequiresPermission({"DELETE_TRAINEES"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Delete Trainee", notes = "Deletes a trainee by username")
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
    @ApiResponse(code = 204, message = "Trainee deleted successfully"),
    @ApiResponse(code = 404, message = "Trainee not found")
  })
  public void deleteByUsername(
      @ApiParam(value = "Username of the trainee to be deleted", required = true)
          @RequestParam("username")
          String username) {

    traineeUpdateUseCase.deleteByUsername(username);
  }

  @PutMapping("/trainees/trainers")
  @Authenticated
  @RequiresPermission({"UPDATE_TRAINEE_TRAINERS"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Update trainers assigned to a trainee",
      notes = "Updates the list of trainers assigned to a specific trainee.")
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
    @ApiResponse(code = 200, message = "Successfully updated trainee's trainers."),
    @ApiResponse(code = 400, message = "Invalid request or validation failed."),
    @ApiResponse(code = 404, message = "Trainee or trainers not found.")
  })
  public List<TrainerUserDetailsResponse> updateTraineeTrainers(
      @ApiParam(value = "Details of the trainee and trainers to update", required = true)
          @RequestBody
          @Valid
          TraineeTrainersUpdateRequest request) {

    return TrainerUserDetailsResponse.from(
        traineeUpdateUseCase.updateTraineeTrainers(request.toCommand()).getTrainers());
  }

  @GetMapping("/trainees/trainings")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINEES_TRAININGS"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Retrieve trainee trainings",
      notes = "Returns a list of trainings for a specific trainee based on the provided criteria.")
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
    @ApiResponse(code = 200, message = "Successfully retrieved trainee trainings."),
    @ApiResponse(code = 404, message = "Trainee or trainings not found."),
    @ApiResponse(code = 401, message = "Unauthorized access."),
    @ApiResponse(
        code = 403,
        message = "Forbidden. You do not have permission to access this resource.")
  })
  public List<TraineeTrainingsResponse> getTraineeTrainings(
      @ApiParam(value = "Trainee's username for filtering", required = true)
          @RequestParam("username")
          String username,
      @ApiParam(value = "Start date for filtering trainings")
          @RequestParam(value = "period_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate periodFrom,
      @ApiParam(value = "End date for filtering trainings")
          @RequestParam(value = "period_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate periodTo,
      @ApiParam(value = "Trainer's name for filtering trainings")
          @RequestParam(value = "trainer_name", required = false)
          String trainerName,
      @ApiParam(value = "Training type for filtering trainings")
          @RequestParam(value = "training_type", required = false)
          String trainingType) {
    return TraineeTrainingsResponse.from(
        loadTrainingUseCase.findTraineeTrainingsByCriteria(
            username, periodFrom, periodTo, trainerName, trainingType));
  }

  @PatchMapping("/trainees/state")
  @Authenticated
  @RequiresPermission({"UPDATE_TRAINEE_STATE"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Update trainee state",
      notes =
          "Allows updating specific state properties of a trainee, such as active status or custom states.")
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
    @ApiResponse(code = 200, message = "Trainee state updated successfully."),
    @ApiResponse(code = 400, message = "Invalid request data."),
    @ApiResponse(code = 404, message = "Trainee not found."),
    @ApiResponse(
        code = 403,
        message = "Forbidden. You do not have permission to perform this action.")
  })
  public void updateTraineeState(
      @ApiParam(value = "Request to update trainee state", required = true) @RequestBody @Valid
          TraineeActivateDeactivateRequest request) {

    traineeUpdateUseCase.activateDeactivate(request.toCommand());
  }
}
