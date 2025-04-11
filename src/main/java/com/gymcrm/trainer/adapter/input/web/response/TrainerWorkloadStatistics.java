package com.gymcrm.trainer.adapter.input.web.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadStatistics {
	private int totalTrainers;
	private int activeTrainers;
	private int totalWorkloadHours;
	private double averageWorkloadHours;
	private int maxWorkloadHours;
	private String mostBusyTrainer;
	private Map<String, Integer> workloadBySpecialization;
}
