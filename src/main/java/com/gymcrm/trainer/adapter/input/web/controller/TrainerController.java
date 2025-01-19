package com.gymcrm.trainer.adapter.input.web.controller;

import com.gymcrm.configuration.security.Authenticated;
import com.gymcrm.configuration.security.RequiresPermission;
import com.gymcrm.trainee.adapter.input.web.response.TraineeTrainingsResponse;
import com.gymcrm.trainer.adapter.input.web.request.TrainerActivateDeactivateRequest;
import com.gymcrm.trainer.adapter.input.web.request.TrainerCreateRequest;
import com.gymcrm.trainer.adapter.input.web.request.TrainerUpdateRequest;
import com.gymcrm.trainer.adapter.input.web.response.TrainerLightResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerUserDetailsResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerUseCase;
import com.gymcrm.trainer.application.port.input.TrainerCreationUseCase;
import com.gymcrm.trainer.application.port.input.TrainerUpdateUseCase;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import io.swagger.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Trainer Management")
public class TrainerController {
  private final TrainerCreationUseCase trainerCreationUseCase;
  private final TrainerUpdateUseCase trainerUpdateUseCase;
  private final LoadTrainingUseCase loadTrainingUseCase;
  private final LoadTrainerUseCase loadTrainerUseCase;

  @Autowired
  public TrainerController(
      TrainerCreationUseCase trainerCreationUseCase,
      TrainerUpdateUseCase trainerUpdateUseCase,
      LoadTrainingUseCase loadTrainingUseCase,
      LoadTrainerUseCase loadTrainerUseCase) {
    this.trainerCreationUseCase = trainerCreationUseCase;
    this.trainerUpdateUseCase = trainerUpdateUseCase;
    this.loadTrainingUseCase = loadTrainingUseCase;
    this.loadTrainerUseCase = loadTrainerUseCase;
  }

  @PostMapping("/users/me/trainers")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create Trainer", notes = "Registers a new trainer in the system")
  @ApiResponses({
    @ApiResponse(code = 201, message = "Trainer created successfully"),
    @ApiResponse(code = 400, message = "Invalid input data")
  })
  public TrainerLightResponse create(
      @ApiParam(value = "Trainer creation details", required = true) @RequestBody @Valid
          TrainerCreateRequest request) {
    Trainer trainer = trainerCreationUseCase.create(request.toCommand());
    TrainerLightResponse response = TrainerLightResponse.from(trainer);
    addCreateLink(response, "self");
    addGetLink(response, trainer.getUser().getUsername(), null);
    addUpdateLink(response, trainer.getId(), null);

    return response;
  }

  @GetMapping("/users/me/trainers")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINERS"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Get Trainer", notes = "Fetches trainer details by username")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "auth_username",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataType = "string"),
    @ApiImplicitParam(
        name = "auth_password",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataType = "string")
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "Trainer details fetched"),
    @ApiResponse(code = 404, message = "Trainer not found"),
    @ApiResponse(code = 401, message = "Unauthorized access")
  })
  public TrainerResponse getByUsername(
      @ApiParam(value = "Trainer username to fetch details", required = true)
          @RequestParam("username")
          String username) {
    Trainer trainer = loadTrainerUseCase.loadByUsername(username);
    TrainerResponse response = TrainerResponse.from(trainer);
    addGetLink(response, trainer.getUser().getUsername(), "self");
    addUpdateLink(response, trainer.getId(), null);
    addUpdateTrainerStateLink(response, null);

    return response;
  }

  @PutMapping("/users/me/trainers/{traineeId}")
  @Authenticated
  @RequiresPermission({"UPDATE_TRAINERS"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Update Trainer", notes = "Updates an existing Trainer's details")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "auth_username",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataType = "string"),
    @ApiImplicitParam(
        name = "auth_password",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataType = "string")
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "Trainer updated successfully"),
    @ApiResponse(code = 400, message = "Invalid data provided"),
    @ApiResponse(code = 404, message = "Trainer not found")
  })
  public TrainerResponse update(
      @ApiParam(value = "Trainer ID", required = true) @PathVariable UUID traineeId,
      @ApiParam(value = "Trainer update details", required = true) @RequestBody @Valid
          TrainerUpdateRequest request) {
    Trainer trainer = trainerUpdateUseCase.update(request.toCommand(traineeId));
    TrainerResponse response = TrainerResponse.from(trainer);
    addUpdateLink(response, traineeId, "self");
    addGetLink(response, trainer.getUser().getUsername(), null);
    addUpdateTrainerStateLink(response, null);

    return response;
  }

  @GetMapping("/users/me/trainers/unassigned")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINERS_NOT_ASSIGNED_TO_TRAINEE"})
  @ApiOperation(
      value = "Get trainers not assigned to any trainee",
      notes = "Returns a list of trainers that are not assigned to a specific trainee.")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "auth_username",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataTypeClass = String.class),
    @ApiImplicitParam(
        name = "auth_password",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataTypeClass = String.class)
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "Successfully retrieved trainers."),
    @ApiResponse(code = 404, message = "Trainer not found."),
    @ApiResponse(code = 401, message = "Unauthorized access.")
  })
  public ResponseEntity<List<TrainerUserDetailsResponse>> getTrainersNotAssignedToTrainee(
      @ApiParam(value = "Username of the trainee to retrieve", required = true)
          @RequestParam("username")
          String username) {
    List<TrainerUserDetailsResponse> responses =
        TrainerUserDetailsResponse.from(
            loadTrainerUseCase.loadActiveTrainersNotAssignedToTrainee(username));
    responses.forEach(
        response -> addGetTrainersNotAssignedToTraineeLink(response, username, "self"));

    return ResponseEntity.ok(responses);
  }

  @GetMapping("/users/me/trainers/trainings")
  @Authenticated
  @RequiresPermission({"VIEW_TRAINER_TRAININGS"})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      value = "Retrieve trainer trainings",
      notes = "Returns a list of trainings for a specific trainer based on the provided criteria.")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "auth_username",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataType = "string"),
    @ApiImplicitParam(
        name = "auth_password",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataType = "string")
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "Successfully retrieved trainer trainings."),
    @ApiResponse(code = 404, message = "Trainer or trainings not found."),
    @ApiResponse(code = 401, message = "Unauthorized access."),
    @ApiResponse(
        code = 403,
        message = "Forbidden. You do not have permission to access this resource.")
  })
  public List<TraineeTrainingsResponse> getTrainerTrainings(
      @ApiParam(value = "Trainer's username for filtering", required = true)
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
      @ApiParam(value = "Trainee's name for filtering trainings")
          @RequestParam(value = "trainee_name", required = false)
          String traineeName) {
    Trainer trainer = loadTrainerUseCase.loadByUsername(username);
    List<TraineeTrainingsResponse> responses =
        TraineeTrainingsResponse.from(
            loadTrainingUseCase.findTrainerTrainingsByCriteria(
                username, periodFrom, periodTo, traineeName));
    responses.forEach(
        response -> {
          addGetTrainerTrainingsLink(response, username, periodFrom, periodTo, traineeName, "self");
          addGetLink(response, username, null);
          addUpdateLink(response, trainer.getId(), null);
        });

    return responses;
  }

  @PatchMapping("/users/me/trainers/state")
  @Authenticated
  @RequiresPermission({"UPDATE_TRAINER_STATE"})
  @ApiOperation(
      value = "Update trainer state",
      notes =
          "Allows updating specific state properties of a trainer, such as active status or custom states.")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "auth_username",
        value = "Authentication username",
        required = true,
        paramType = "header",
        dataType = "string"),
    @ApiImplicitParam(
        name = "auth_password",
        value = "Authentication password",
        required = true,
        paramType = "header",
        dataType = "string")
  })
  @ApiResponses({
    @ApiResponse(code = 200, message = "Trainer state updated successfully."),
    @ApiResponse(code = 400, message = "Invalid request data."),
    @ApiResponse(code = 404, message = "Trainer not found."),
    @ApiResponse(
        code = 403,
        message = "Forbidden. You do not have permission to perform this action.")
  })
  public ResponseEntity<Void> updateTrainerState(
      @ApiParam(value = "Request to update Trainer state", required = true) @RequestBody @Valid
          TrainerActivateDeactivateRequest request) {
    trainerUpdateUseCase.activateDeactivate(request.toCommand());
    RepresentationModel<?> response = new RepresentationModel<>();
    addUpdateTrainerStateLink(response, "self");
    addGetLink(response, request.getUsername(), null);

    return ResponseEntity.noContent().header("Links", response.getLinks().toString()).build();
  }

  private void addCreateLink(RepresentationModel<?> response, String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class)
                    .create(new TrainerCreateRequest()))
            .withRel(defineMethodName(self, "create")));
  }

  private void addGetLink(RepresentationModel<?> response, String username, String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class).getByUsername(username))
            .withRel(defineMethodName(self, "get")));
  }

  private void addUpdateLink(RepresentationModel<?> response, UUID trainerId, String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class)
                    .update(trainerId, new TrainerUpdateRequest()))
            .withRel(defineMethodName(self, "update")));
  }

  private void addGetTrainersNotAssignedToTraineeLink(
      RepresentationModel<?> response, String username, String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class)
                    .getTrainersNotAssignedToTrainee(username))
            .withRel(defineMethodName(self, "get trainers not assigned to trainee")));
  }

  private void addGetTrainerTrainingsLink(
      RepresentationModel<?> response,
      String username,
      LocalDate periodFrom,
      LocalDate periodTo,
      String traineeName,
      String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class)
                    .getTrainerTrainings(username, periodFrom, periodTo, traineeName))
            .withRel(defineMethodName(self, "get trainer trainings")));
  }

  private void addUpdateTrainerStateLink(RepresentationModel<?> response, String self) {
    response.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrainerController.class)
                    .updateTrainerState(new TrainerActivateDeactivateRequest()))
            .withRel(defineMethodName(self, "update trainer state")));
  }

  private String defineMethodName(String self, String name) {
    return self == null ? name : self;
  }
}
