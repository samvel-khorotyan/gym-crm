package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadStatistics;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadDashboardUseCase;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

		List<Trainer> activeTrainers = getActiveTrainers(trainers);
		int activeTrainersCount = activeTrainers.size();

		Map<Trainer, Integer> trainerWorkloadMap = calculateTrainerWorkloads(activeTrainers, currentYear, currentMonth);

		int totalWorkloadHours = calculateTotalWorkloadHours(trainerWorkloadMap);
		double averageWorkloadHours = calculateAverageWorkload(totalWorkloadHours, activeTrainersCount);

		MostBusyTrainerInfo mostBusyTrainerInfo = findMostBusyTrainer(trainerWorkloadMap);

		Map<String, Integer> workloadBySpecialization = aggregateWorkloadBySpecialization(trainerWorkloadMap);

		return buildWorkloadStatistics(totalTrainers, activeTrainersCount, totalWorkloadHours, averageWorkloadHours,
		        mostBusyTrainerInfo.name(), mostBusyTrainerInfo.workloadHours(), workloadBySpecialization);
	}

	private List<Trainer> getActiveTrainers(List<Trainer> trainers) {
		return trainers.stream().filter(trainer -> trainer.getUser().getIsActive()).collect(Collectors.toList());
	}

	private Map<Trainer, Integer> calculateTrainerWorkloads(List<Trainer> activeTrainers, int year, int month) {
		return activeTrainers.stream().collect(
		        Collectors.toMap(trainer -> trainer, trainer -> getTrainerWorkloadHours(trainer, year, month)));
	}

	private int getTrainerWorkloadHours(Trainer trainer, int year, int month) {
		String username = trainer.getUser().getUsername();
		try {
			TrainerMonthlyWorkloadResponse workload = loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload(username,
			        year, month);
			return workload.getSummaryDuration() / 60; // Convert minutes to hours
		} catch (Exception e) {
			log.warn("Failed to get workload for trainer: {}, Error: {}", username, e.getMessage());
			return 0;
		}
	}

	private int calculateTotalWorkloadHours(Map<Trainer, Integer> trainerWorkloadMap) {
		return trainerWorkloadMap.values().stream().mapToInt(Integer::intValue).sum();
	}

	private double calculateAverageWorkload(int totalWorkloadHours, int activeTrainersCount) {
		return activeTrainersCount > 0 ? (double) totalWorkloadHours / activeTrainersCount : 0;
	}

	private MostBusyTrainerInfo findMostBusyTrainer(Map<Trainer, Integer> trainerWorkloadMap) {
		return trainerWorkloadMap.entrySet().stream().max(Map.Entry.comparingByValue())
		        .map(entry -> new MostBusyTrainerInfo(getTrainerFullName(entry.getKey()), entry.getValue()))
		        .orElse(new MostBusyTrainerInfo("", 0));
	}

	private String getTrainerFullName(Trainer trainer) {
		return trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
	}

	private Map<String, Integer> aggregateWorkloadBySpecialization(Map<Trainer, Integer> trainerWorkloadMap) {
		return trainerWorkloadMap.entrySet().stream().collect(Collectors
		        .groupingBy(entry -> entry.getKey().getSpecialization(), Collectors.summingInt(Map.Entry::getValue)));
	}

	private TrainerWorkloadStatistics buildWorkloadStatistics(int totalTrainers, int activeTrainers,
	        int totalWorkloadHours, double averageWorkloadHours, String mostBusyTrainer, int maxWorkloadHours,
	        Map<String, Integer> workloadBySpecialization) {

		return TrainerWorkloadStatistics.builder().totalTrainers(totalTrainers).activeTrainers(activeTrainers)
		        .totalWorkloadHours(totalWorkloadHours).averageWorkloadHours(averageWorkloadHours)
		        .maxWorkloadHours(maxWorkloadHours).mostBusyTrainer(mostBusyTrainer)
		        .workloadBySpecialization(workloadBySpecialization).build();
	}

	private record MostBusyTrainerInfo(String name, int workloadHours) {
	}
}
