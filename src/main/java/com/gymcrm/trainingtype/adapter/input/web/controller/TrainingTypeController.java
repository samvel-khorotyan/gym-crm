package com.gymcrm.trainingtype.adapter.input.web.controller;

import com.gymcrm.trainingtype.adapter.input.web.response.TrainingTypeResponse;
import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import io.swagger.annotations.*;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "Training Type Management")
public class TrainingTypeController {
	private static final Logger logger = LoggerFactory.getLogger(TrainingTypeController.class);

	private final LoadTrainingTypeUseCase loadTrainingTypeUseCase;

	@GetMapping("/users/me/training-types")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Retrieve all training types",notes = "Returns a list of all available training types.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved training types."),
	        @ApiResponse(code = 401,message = "Unauthorized access."),
	        @ApiResponse(code = 403,message = "Forbidden. You do not have permission to view training types.")})
	public List<TrainingTypeResponse> getTrainingTypes() {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Received GET request to retrieve all training types.", transactionId);

		List<TrainingType> trainingTypes = loadTrainingTypeUseCase.loadAll();

		logger.info("Transaction ID: {} - Successfully retrieved {} training types.", transactionId,
		        trainingTypes.size());

		MDC.clear();
		return TrainingTypeResponse.from(trainingTypes);
	}
}
