package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadSummary;
import com.gymcrm.trainer.application.port.input.LoadTrainerSummaryUseCase;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerSummaryService implements LoadTrainerSummaryUseCase {
	private final LoadTrainerPort loadTrainerPort;
	private final LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@Override
	public List<TrainerWorkloadSummary> loadAllTrainersWithWorkload() {
		List<Trainer> trainers = loadTrainerPort.findAll();

		return trainers.stream().map(trainer -> {
			String username = trainer.getUser().getUsername();
			TrainerMonthlyWorkloadResponse workload;
			try {
				workload = loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username);
			} catch (Exception e) {
				log.warn("Failed to get workload for trainer: {}, Error: {}", username, e.getMessage());
				workload = TrainerMonthlyWorkloadResponse.builder().summaryDuration(0).build();
			}

			return TrainerWorkloadSummary.builder().username(username)
			        .fullName(trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName())
			        .specialization(trainer.getSpecialization()).isActive(trainer.getUser().getIsActive())
			        .currentMonthWorkload(workload.getSummaryDuration()).build();
		}).collect(Collectors.toList());
	}
}
