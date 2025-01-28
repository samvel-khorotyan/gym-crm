package com.gymcrm.training.adapter.input.web.controller;

import com.gymcrm.configuration.security.Authenticated;
import com.gymcrm.configuration.security.RequiresPermission;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import io.swagger.annotations.*;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Training Management")
public class TrainingController {
	private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

	private final TrainingCreationUseCase trainingCreationUseCase;

	public TrainingController(TrainingCreationUseCase trainingCreationUseCase) {
		this.trainingCreationUseCase = trainingCreationUseCase;
	}

	@PostMapping("/users/me/trainings")
	@Authenticated
	@RequiresPermission({"CREATE_TRAINING"})
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new training",notes = "Creates a new training session based on the provided details.")
	@ApiImplicitParams({
	        @ApiImplicitParam(name = "auth_username",value = "Authentication username",required = true,paramType = "header",dataType = "string"),
	        @ApiImplicitParam(name = "auth_password",value = "Authentication password",required = true,paramType = "header",dataType = "string")})
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
}
