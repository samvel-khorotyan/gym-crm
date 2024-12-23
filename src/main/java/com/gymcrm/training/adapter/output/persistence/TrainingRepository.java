package com.gymcrm.training.adapter.output.persistence;

import com.gymcrm.training.application.exception.TrainingNotFoundException;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
  public Training findById(UUID id) {
    return repository.findById(id).orElseThrow(() -> TrainingNotFoundException.by(id));
  }

  @Override
  public List<Training> findAll() {
    return repository.findAll();
  }

  @Override
  public void save(Training training) {
    repository.save(training);
  }

  @Override
  public List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
    return repository.findAll(
        TraineeTrainingSpecification.filterByCriteria(
            startDate, endDate, trainerName, trainingType));
  }

  @Override
  public List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName) {
    return repository.findAll(
        TrainerTrainingSpecification.filterByCriteria(startDate, endDate, traineeName));
  }

  private static class TraineeTrainingSpecification {
    public static Specification<Training> filterByCriteria(
        LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
      return (root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null && endDate != null) {
          predicates.add(criteriaBuilder.between(root.get("trainingDate"), startDate, endDate));
        }

        if (trainerName != null && !trainerName.isEmpty()) {
          predicates.add(
              criteriaBuilder.like(
                  criteriaBuilder.concat(
                      criteriaBuilder.concat(root.get("trainer").get("user").get("firstName"), " "),
                      root.get("trainer").get("user").get("lastName")),
                  "%" + trainerName + "%"));
        }

        if (trainingType != null && !trainingType.isEmpty()) {
          predicates.add(
              criteriaBuilder.equal(
                  root.get("trainingType").get("trainingTypeName"), trainingType));
        }

        return predicates.stream()
            .reduce(criteriaBuilder::and)
            .orElse(criteriaBuilder.conjunction());
      };
    }
  }

  private static class TrainerTrainingSpecification {
    public static Specification<Training> filterByCriteria(
        LocalDate startDate, LocalDate endDate, String traineeName) {
      return (root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null && endDate != null) {
          predicates.add(criteriaBuilder.between(root.get("trainingDate"), startDate, endDate));
        }

        if (traineeName != null && !traineeName.isEmpty()) {
          predicates.add(
              criteriaBuilder.like(
                  criteriaBuilder.concat(
                      criteriaBuilder.concat(root.get("trainee").get("user").get("firstName"), " "),
                      root.get("trainee").get("user").get("lastName")),
                  "%" + traineeName + "%"));
        }

        return predicates.stream()
            .reduce(criteriaBuilder::and)
            .orElse(criteriaBuilder.conjunction());
      };
    }
  }
}
