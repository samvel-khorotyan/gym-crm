package com.gymcrm.trainer.adapter.input.web.controller;

import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadSummary;
import com.gymcrm.trainer.application.port.input.LoadTrainerSummaryUseCase;
import io.swagger.annotations.*;
import java.util.List;
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
@Api(tags = "Trainer Management")
public class TrainerSummaryController {
	private static final Logger logger = LoggerFactory.getLogger(TrainerSummaryController.class);

	private final LoadTrainerSummaryUseCase loadTrainerSummaryUseCase;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users/me/trainers/with-workload")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get all trainers with workload",notes = "Retrieves all trainers with their current month workload.")
	@ApiResponses({@ApiResponse(code = 200,message = "Successfully retrieved trainers with workload.")})
	public List<TrainerWorkloadSummary> getAllTrainersWithWorkload() {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		logger.info("Transaction ID: {} - Getting all trainers with workload", transactionId);

		List<TrainerWorkloadSummary> response = loadTrainerSummaryUseCase.loadAllTrainersWithWorkload();

		logger.info("Transaction ID: {} - Successfully retrieved {} trainers with workload", transactionId,
		        response.size());

		MDC.clear();
		return response;
	}
}
