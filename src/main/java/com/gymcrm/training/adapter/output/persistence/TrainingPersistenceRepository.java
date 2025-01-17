package com.gymcrm.training.adapter.output.persistence;

import com.gymcrm.training.domain.Training;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainingPersistenceRepository
    extends JpaRepository<Training, UUID>, JpaSpecificationExecutor<Training> {
  boolean existsByTraineeUserUsername(String username);

  boolean existsByTrainerUserUsername(String username);

  @Modifying
  @Query("DELETE FROM Training t WHERE t.trainee.id = :traineeId")
  void deleteAllByTraineeId(UUID traineeId);

  @Query("SELECT t FROM Training t WHERE t.trainer.user.username IN :usernames")
  List<Training> findAllByTrainerUsernames(@Param("usernames") List<String> usernames);
}
