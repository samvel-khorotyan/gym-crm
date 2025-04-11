package com.gymcrm.training.application.port.input;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingUseCase {
	List<Training> loadAll();

	List<Training> findTraineeTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String trainerName, String trainingType);

	List<Training> findTrainerTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String traineeName);

	Training findById(UUID id);
}
