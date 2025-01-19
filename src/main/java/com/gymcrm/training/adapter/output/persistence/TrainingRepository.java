package com.gymcrm.training.adapter.output.persistence;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingRepository implements UpdateTrainingPort, LoadTrainingPort {
	private final TrainingPersistenceRepository repository;

	@Autowired
	public TrainingRepository(TrainingPersistenceRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Training> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Training> findAllByTrainerUsernames(List<String> usernames) {
		return repository.findAllByTrainerUsernames(usernames);
	}

	@Override
	public void save(Training training) {
		repository.save(training);
	}

	@Override
	public void saveAll(List<Training> trainings) {
		repository.saveAll(trainings);
	}

	@Override
	public void deleteByTraineeId(UUID traineeId) {
		repository.deleteAllByTraineeId(traineeId);
	}

	@Override
	public List<Training> findTraineeTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String trainerName, String trainingType) {
		if (!repository.existsByTraineeUserUsername(username))
			throw TraineeNotFoundException.by(username);

		return repository.findAll(
		        TraineeTrainingSpecification.filterByCriteria(username, startDate, endDate, trainerName, trainingType));
	}

	@Override
	public List<Training> findTrainerTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String traineeName) {
		if (!repository.existsByTrainerUserUsername(username))
			throw TrainerNotFoundException.by(username);

		return repository
		        .findAll(TrainerTrainingSpecification.filterByCriteria(username, startDate, endDate, traineeName));
	}

	private static class TraineeTrainingSpecification {
		public static Specification<Training> filterByCriteria(String username, LocalDate startDate, LocalDate endDate,
		        String trainerName, String trainingType) {
			return (root, query, criteriaBuilder) -> {
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.equal(root.get("trainee").get("user").get("username"), username));

				if (startDate != null && endDate != null) {
					predicates.add(criteriaBuilder.between(root.get("trainingDate"), startDate, endDate));
				}

				if (trainerName != null && !trainerName.isEmpty()) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.concat(
					        criteriaBuilder.concat(root.get("trainer").get("user").get("firstName"), " "),
					        root.get("trainer").get("user").get("lastName")), "%" + trainerName + "%"));
				}

				if (trainingType != null && !trainingType.isEmpty()) {
					predicates
					        .add(criteriaBuilder.equal(root.get("trainingType").get("trainingTypeName"), trainingType));
				}

				return predicates.stream().reduce(criteriaBuilder::and).orElse(criteriaBuilder.conjunction());
			};
		}
	}

	private static class TrainerTrainingSpecification {
		public static Specification<Training> filterByCriteria(String username, LocalDate startDate, LocalDate endDate,
		        String traineeName) {
			return (root, query, criteriaBuilder) -> {
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.equal(root.get("trainer").get("user").get("username"), username));

				if (startDate != null && endDate != null) {
					predicates.add(criteriaBuilder.between(root.get("trainingDate"), startDate, endDate));
				}

				if (traineeName != null && !traineeName.isEmpty()) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.concat(
					        criteriaBuilder.concat(root.get("trainee").get("user").get("firstName"), " "),
					        root.get("trainee").get("user").get("lastName")), "%" + traineeName + "%"));
				}

				return predicates.stream().reduce(criteriaBuilder::and).orElse(criteriaBuilder.conjunction());
			};
		}
	}
}
