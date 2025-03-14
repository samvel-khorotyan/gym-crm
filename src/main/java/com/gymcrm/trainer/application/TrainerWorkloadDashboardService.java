package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadStatistics;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadDashboardUseCase;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadDashboardService implements LoadTrainerWorkloadDashboardUseCase {
	private final LoadTrainerPort loadTrainerPort;
	private final LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@Override
	public TrainerWorkloadStatistics loadTrainerWorkloadStatistics() {
		List<Trainer> trainers = loadTrainerPort.findAll();
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();
		int currentMonth = now.getMonthValue();

		int totalTrainers = trainers.size();
		int activeTrainers = 0;
		int totalWorkloadHours = 0;
		int maxWorkloadHours = 0;
		String mostBusyTrainer = "";
		Map<String, Integer> workloadBySpecialization = new HashMap<>();

		for (Trainer trainer : trainers) {
			if (trainer.getUser().getIsActive()) {
				activeTrainers++;

				String username = trainer.getUser().getUsername();
				try {
					TrainerMonthlyWorkloadResponse workload = loadTrainerWorkloadUseCase
					        .loadTrainerMonthlyWorkload(username, currentYear, currentMonth);

					int workloadHours = workload.getSummaryDuration() / 60; // Convert minutes to hours
					totalWorkloadHours += workloadHours;

					if (workloadHours > maxWorkloadHours) {
						maxWorkloadHours = workloadHours;
						mostBusyTrainer = trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
					}

					// Aggregate by specialization
					String specialization = trainer.getSpecialization();
					workloadBySpecialization.put(specialization,
					        workloadBySpecialization.getOrDefault(specialization, 0) + workloadHours);

				} catch (Exception e) {
					log.warn("Failed to get workload for trainer: {}, Error: {}", username, e.getMessage());
				}
			}
		}

		double averageWorkloadHours = activeTrainers > 0 ? (double) totalWorkloadHours / activeTrainers : 0;

		return TrainerWorkloadStatistics.builder().totalTrainers(totalTrainers).activeTrainers(activeTrainers)
		        .totalWorkloadHours(totalWorkloadHours).averageWorkloadHours(averageWorkloadHours)
		        .maxWorkloadHours(maxWorkloadHours).mostBusyTrainer(mostBusyTrainer)
		        .workloadBySpecialization(workloadBySpecialization).build();
	}
}
