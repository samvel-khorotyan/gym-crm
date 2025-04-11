package com.gymcrm.training.adapter.input.web.controller;

import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.input.UpdateTrainingUseCase;
import io.swagger.annotations.*;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "Training Management")
public class TrainingController {
	private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

	private final TrainingCreationUseCase trainingCreationUseCase;
	private final UpdateTrainingUseCase updateTrainingUseCase;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/users/me/trainings")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new training",notes = "Creates a new training session based on the provided details.")
	@ApiResponses({@ApiResponse(code = 201,message = "Training created successfully."),
	        @ApiResponse(code = 400,message = "Invalid input data provided.")})
	public void create(
	        @ApiParam(value = "Details required to create a training session",required = true) @RequestBody @Valid TrainingCreateRequest request) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received POST request to create training: {}", transactionId,
		        request.getTrainingName());

		trainingCreationUseCase.create(request.toCommand());

		logger.info("Transaction ID: {} - Successfully created training: {}", transactionId, request.getTrainingName());

		MDC.clear();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/trainings/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Delete a training",notes = "Deletes a training session by ID.")
	@ApiResponses({@ApiResponse(code = 204,message = "Training deleted successfully."),
	        @ApiResponse(code = 404,message = "Training not found.")})
	public void delete(@PathVariable UUID id) {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received DELETE request for training with ID: {}", transactionId, id);

		updateTrainingUseCase.deleteTraining(id);

		logger.info("Transaction ID: {} - Successfully deleted training with ID: {}", transactionId, id);

		MDC.clear();
	}
}
