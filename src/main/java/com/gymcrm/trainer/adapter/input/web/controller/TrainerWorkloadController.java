package com.gymcrm.trainer.adapter.input.web.controller;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
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
@RequiredArgsConstructor
@Api(tags = "Trainer Workload Management")
public class TrainerWorkloadController {
	private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadController.class);

	private final LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
	@GetMapping("/users/me/trainers/{username}/workload/{year}/{month}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get trainer monthly workload",notes = "Retrieves the monthly workload summary for a trainer.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainer monthly workload."),
	        @ApiResponse(code = 404,message = "Trainer not found.")})
	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(
	        @ApiParam(value = "Trainer's username",required = true) @PathVariable String username,
	        @ApiParam(value = "Year",required = true) @PathVariable int year,
	        @ApiParam(value = "Month",required = true) @PathVariable int month) {

		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Getting monthly workload for trainer: {}, year: {}, month: {}", transactionId,
		        username, year, month);

		TrainerMonthlyWorkloadResponse response = loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload(username, year,
		        month);

		logger.info("Transaction ID: {} - Successfully retrieved monthly workload for trainer: {}", transactionId,
		        username);

		MDC.clear();
		return response;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
	@GetMapping("/users/me/trainers/{username}/workload/current")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get trainer current month workload",notes = "Retrieves the current month workload summary for a trainer.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainer current month workload."),
	        @ApiResponse(code = 404,message = "Trainer not found.")})
	public TrainerMonthlyWorkloadResponse getTrainerCurrentMonthWorkload(
	        @ApiParam(value = "Trainer's username",required = true) @PathVariable String username) {

		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Getting current month workload for trainer: {}", transactionId, username);

		TrainerMonthlyWorkloadResponse response = loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username);

		logger.info("Transaction ID: {} - Successfully retrieved current month workload for trainer: {}", transactionId,
		        username);

		MDC.clear();
		return response;
	}
}
