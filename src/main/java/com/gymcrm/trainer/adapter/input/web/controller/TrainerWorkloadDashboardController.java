package com.gymcrm.trainer.adapter.input.web.controller;

import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadStatistics;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadDashboardUseCase;
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
@Api(tags = "Trainer Dashboard")
@RequiredArgsConstructor
public class TrainerWorkloadDashboardController {
	private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadDashboardController.class);

	private final LoadTrainerWorkloadDashboardUseCase loadTrainerWorkloadDashboardUseCase;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users/me/trainers/dashboard/trainer-workload")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get trainer workload statistics",notes = "Retrieves statistical data about trainers workload.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainer workload statistics.")})
	public TrainerWorkloadStatistics getTrainerWorkloadStatistics() {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Getting trainer workload statistics", transactionId);

		TrainerWorkloadStatistics response = loadTrainerWorkloadDashboardUseCase.loadTrainerWorkloadStatistics();

		logger.info("Transaction ID: {} - Successfully retrieved trainer workload statistics", transactionId);

		MDC.clear();
		return response;
	}
}
