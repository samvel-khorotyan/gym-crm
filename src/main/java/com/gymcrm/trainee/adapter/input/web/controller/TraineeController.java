package com.gymcrm.trainee.adapter.input.web.controller;

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
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.adapter.input.web.response.TrainerUserDetailsResponse;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import io.swagger.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Trainee Management")
public class TraineeController {
	private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);

	private final TraineeCreationUseCase traineeCreationUseCase;
	private final TraineeUpdateUseCase traineeUpdateUseCase;
	private final LoadTrainingUseCase loadTrainingUseCase;
	private final LoadTraineeUseCase loadTraineeUseCase;

	public TraineeController(TraineeCreationUseCase traineeCreationUseCase, TraineeUpdateUseCase traineeUpdateUseCase,
	        LoadTrainingUseCase loadTrainingUseCase, LoadTraineeUseCase loadTraineeUseCase) {
		this.traineeCreationUseCase = traineeCreationUseCase;
		this.traineeUpdateUseCase = traineeUpdateUseCase;
		this.loadTrainingUseCase = loadTrainingUseCase;
		this.loadTraineeUseCase = loadTraineeUseCase;
	}

	@PostMapping("/users/me/trainees")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create Trainee",notes = "Registers a new trainee in the system")
	@ApiResponses({@ApiResponse(code = 201,message = "Trainee created successfully"),
	        @ApiResponse(code = 400,message = "Invalid input data")})
	public TraineeLightResponse create(
	        @ApiParam(value = "Trainee creation details",required = true) @RequestBody @Valid TraineeCreateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Creating trainee with details: {}", transactionId, request);

		Trainee trainee = traineeCreationUseCase.create(request.toCommand());
		TraineeLightResponse response = TraineeLightResponse.from(trainee);

		String username = trainee.getUser().getUsername();
		addGetLink(response, username, null);
		addUpdateLink(response, trainee.getId(), null);
		addDeleteLink(response, username, null);

		logger.info("Transaction ID: {} - Trainee created successfully with username: {}", transactionId, username);

		MDC.clear();
		return response;
	}

	@GetMapping("/users/me/trainees")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Trainee",notes = "Fetches trainee details by username")
	@ApiResponses({@ApiResponse(code = 200,message = "Trainee details fetched"),
	        @ApiResponse(code = 404,message = "Trainee not found"),
	        @ApiResponse(code = 401,message = "Unauthorized access")})
	public TraineeResponse getByUsername(@RequestParam("username") String username) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received GET request with username: {}", transactionId, username);

		Trainee trainee = loadTraineeUseCase.loadByUsername(username);

		logger.info("Transaction ID: {} - Successfully fetched trainee: {}", transactionId, username);

		TraineeResponse response = TraineeResponse.from(trainee);
		addGetLink(response, trainee.getUser().getUsername(), "self");
		addUpdateLink(response, trainee.getId(), null);
		addDeleteLink(response, trainee.getUser().getUsername(), null);

		MDC.clear();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/users/me/trainees/{traineeId}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Update Trainee",notes = "Updates an existing trainee's details")
	@ApiResponses({@ApiResponse(code = 200,message = "Trainee updated successfully"),
	        @ApiResponse(code = 400,message = "Invalid data provided"),
	        @ApiResponse(code = 404,message = "Trainee not found")})
	public TraineeResponse update(@ApiParam(value = "Trainee ID",required = true) @PathVariable UUID traineeId,
	        @ApiParam(value = "Trainee update details",required = true) @RequestBody @Valid TraineeUpdateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received PUT request for updating trainee with ID: {}", transactionId,
		        traineeId);

		Trainee trainee = traineeUpdateUseCase.update(request.toCommand(traineeId));
		TraineeResponse response = TraineeResponse.from(trainee);
		addGetLink(response, trainee.getUser().getUsername(), null);
		addDeleteLink(response, trainee.getUser().getUsername(), null);

		logger.info("Transaction ID: {} - Successfully updated trainee with ID: {}", transactionId, traineeId);

		MDC.clear();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/users/me/trainees")
	@ApiOperation(value = "Delete Trainee",notes = "Deletes a trainee by username")
	@ApiResponses({@ApiResponse(code = 204,message = "Trainee deleted successfully"),
	        @ApiResponse(code = 404,message = "Trainee not found")})
	public ResponseEntity<RepresentationModel<?>> deleteByUsername(
	        @ApiParam(value = "Username of the trainee to be deleted",required = true) @RequestParam("username") String username) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received DELETE request for trainee with username: {}", transactionId,
		        username);

		traineeUpdateUseCase.deleteByUsername(username);

		RepresentationModel<?> model = new RepresentationModel<>();
		addCreateLink(model, null);

		logger.info("Transaction ID: {} - Successfully deleted trainee with username: {}", transactionId, username);

		MDC.clear();
		return ResponseEntity.noContent().header("Links", model.getLinks().toString()).build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/users/me/trainees/trainers")
	@ApiOperation(value = "Update trainers assigned to a trainee",notes = "Updates the list of trainers assigned to a specific trainee.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully updated trainee's trainers."),
	        @ApiResponse(code = 400,message = "Invalid request or validation failed."),
	        @ApiResponse(code = 404,message = "Trainee or trainers not found.")})
	public ResponseEntity<List<TrainerUserDetailsResponse>> updateTraineeTrainers(
	        @ApiParam(value = "Details of the trainee and trainers to update",required = true) @RequestBody @Valid TraineeTrainersUpdateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received PUT request to update trainers for trainee: {}", transactionId,
		        request.getTraineeUsername());

		List<TrainerUserDetailsResponse> responses = TrainerUserDetailsResponse
		        .from(traineeUpdateUseCase.updateTraineeTrainers(request.toCommand()).getTrainers());
		responses.forEach(response -> {
			addUpdateTraineeTrainersLink(response, null);
			addGetLink(response, response.getUsername(), null);
		});

		logger.info("Transaction ID: {} - Successfully updated trainers for trainee: {}", transactionId,
		        request.getTraineeUsername());

		MDC.clear();
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/users/me/trainees/trainings")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Retrieve trainee trainings",notes = "Returns a list of trainings for a specific trainee based on the provided criteria.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainee trainings."),
	        @ApiResponse(code = 404,message = "Trainee or trainings not found."),
	        @ApiResponse(code = 401,message = "Unauthorized access."),
	        @ApiResponse(code = 403,message = "Forbidden. You do not have permission to access this resource.")})
	public List<TraineeTrainingsResponse> getTraineeTrainings(
	        @ApiParam(value = "Trainee's username for filtering",required = true) @RequestParam("username") String username,
	        @ApiParam(value = "Start date for filtering trainings") @RequestParam(value = "period_from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
	        @ApiParam(value = "End date for filtering trainings") @RequestParam(value = "period_to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
	        @ApiParam(value = "Trainer's name for filtering trainings") @RequestParam(value = "trainer_name",required = false) String trainerName,
	        @ApiParam(value = "Training type for filtering trainings") @RequestParam(value = "training_type",required = false) String trainingType) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received GET request to fetch trainings for trainee: {}", transactionId,
		        username);

		List<TraineeTrainingsResponse> responses = TraineeTrainingsResponse.from(loadTrainingUseCase
		        .findTraineeTrainingsByCriteria(username, periodFrom, periodTo, trainerName, trainingType));

		responses.forEach(response -> {
			addGetTraineeTrainingsLink(response, username, periodFrom, periodTo, trainerName, trainingType, "self");
			addUpdateTraineeTrainersLink(response, null);
			addCreateLink(response, null);
		});

		logger.info("Transaction ID: {} - Successfully fetched trainings for trainee: {}", transactionId, username);

		MDC.clear();
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/users/me/trainees/state")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Update trainee state",notes = "Allows updating specific state properties of a trainee, such as active status or custom states.")
	@ApiResponses({@ApiResponse(code = 200,message = "Trainee state updated successfully."),
	        @ApiResponse(code = 400,message = "Invalid request data."),
	        @ApiResponse(code = 404,message = "Trainee not found."),
	        @ApiResponse(code = 403,message = "Forbidden. You do not have permission to perform this action.")})
	public ResponseEntity<Void> updateTraineeState(
	        @ApiParam(value = "Request to update trainee state",required = true) @RequestBody @Valid TraineeActivateDeactivateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received PATCH request to update state for trainee: {}", transactionId,
		        request.getUsername());

		traineeUpdateUseCase.activateDeactivate(request.toCommand());

		RepresentationModel<?> response = new RepresentationModel<>();
		addUpdateTraineeStateLink(response, "self");
		addGetLink(response, request.getUsername(), null);
		addDeleteLink(response, request.getUsername(), null);

		logger.info("Transaction ID: {} - Successfully updated state for trainee: {}", transactionId,
		        request.getUsername());

		MDC.clear();
		return ResponseEntity.noContent().header("Links", response.getLinks().toString()).build();
	}

	private void addCreateLink(RepresentationModel<?> response, String self) {
		response.add(WebMvcLinkBuilder
		        .linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class).create(new TraineeCreateRequest()))
		        .withRel(defineMethodName(self, "create")));
	}

	private void addGetLink(RepresentationModel<?> response, String username, String self) {
		response.add(
		        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class).getByUsername(username))
		                .withRel(defineMethodName(self, "get")));
	}

	private void addUpdateLink(RepresentationModel<?> response, UUID traineeId, String self) {
		response.add(WebMvcLinkBuilder.linkTo(
		        WebMvcLinkBuilder.methodOn(TraineeController.class).update(traineeId, new TraineeUpdateRequest()))
		        .withRel(defineMethodName(self, "update")));
	}

	private void addDeleteLink(RepresentationModel<?> response, String username, String self) {
		response.add(
		        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class).deleteByUsername(username))
		                .withRel(defineMethodName(self, "delete")));
	}

	private void addUpdateTraineeTrainersLink(RepresentationModel<?> response, String self) {
		response.add(WebMvcLinkBuilder
		        .linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class)
		                .updateTraineeTrainers(new TraineeTrainersUpdateRequest()))
		        .withRel(defineMethodName(self, "update trainee trainers")));
	}

	private void addGetTraineeTrainingsLink(RepresentationModel<?> response, String username, LocalDate periodFrom,
	        LocalDate periodTo, String trainerName, String trainingType, String self) {
		response.add(
		        WebMvcLinkBuilder
		                .linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class).getTraineeTrainings(username,
		                        periodFrom, periodTo, trainerName, trainingType))
		                .withRel(defineMethodName(self, "get trainee trainings")));
	}

	private void addUpdateTraineeStateLink(RepresentationModel<?> response, String self) {
		response.add(WebMvcLinkBuilder
		        .linkTo(WebMvcLinkBuilder.methodOn(TraineeController.class)
		                .updateTraineeState(new TraineeActivateDeactivateRequest()))
		        .withRel(defineMethodName(self, "update trainee state")));
	}

	private String defineMethodName(String self, String name) {
		return self == null ? name : self;
	}
}
