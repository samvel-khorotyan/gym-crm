package com.gymcrm.training.application.port.output;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingPort {
	List<Training> findAll();

	List<Training> findAllByTrainerUsernames(List<String> usernames);

	List<Training> findTraineeTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String trainerName, String trainingType);

	List<Training> findTrainerTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String traineeName);

	Training findById(UUID id);

	boolean existsByTraineeAndTrainer(UUID traineeId, UUID trainerId);
}
