package com.gymcrm.trainer.adapter.input.web.controller;

import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsWithWorkloadResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerDetailsUseCase;
import io.swagger.annotations.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Trainer Management")
@RequiredArgsConstructor
public class TrainerDetailsController {
	private static final Logger logger = LoggerFactory.getLogger(TrainerDetailsController.class);

	private final LoadTrainerDetailsUseCase loadTrainerDetailsUseCase;

	@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
	@GetMapping("/users/me/trainers/{username}/details-with-workload")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get trainer details with workload",notes = "Retrieves trainer details including current month workload.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainer details with workload."),
	        @ApiResponse(code = 404,message = "Trainer not found.")})
	public TrainerDetailsWithWorkloadResponse getTrainerDetailsWithWorkload(
	        @ApiParam(value = "Trainer's username",required = true) @PathVariable String username) {

		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Getting details with workload for trainer: {}", transactionId, username);

		TrainerDetailsWithWorkloadResponse response = loadTrainerDetailsUseCase
		        .loadTrainerDetailsWithWorkload(username);

		logger.info("Transaction ID: {} - Successfully retrieved details with workload for trainer: {}", transactionId,
		        username);

		MDC.clear();
		return response;
	}
}
