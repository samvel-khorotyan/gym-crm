package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainerPersistenceRepository extends JpaRepository<Trainer, UUID> {
  Optional<Trainer> findByUserUsername(String username);

  @Query(
      """
          SELECT t
          FROM Trainer t
          LEFT JOIN FETCH t.trainees trainees
          JOIN FETCH t.user user
          WHERE user.username = :username
            AND (trainees IS NOT NULL OR trainees IS NULL)
          """)
  Optional<Trainer> findByUsernameWithTrainees(@Param("username") String username);

  @Query(
      """
          SELECT t
          FROM Trainer t
          LEFT JOIN FETCH t.trainees trainees
          JOIN FETCH t.user user
          WHERE t.id = :id
            AND (trainees IS NOT NULL OR trainees IS NULL)
          """)
  Optional<Trainer> findByIdWithTrainees(@Param("id") UUID id);

  @Query(
      """
          SELECT DISTINCT t
          FROM Trainer t
          LEFT JOIN FETCH t.trainees trainees
          WHERE NOT EXISTS (
              SELECT 1
              FROM Training tr
              WHERE tr.trainer.id = t.id
                AND tr.trainee.user.username = :username
                AND tr.trainee.user.isActive = true
          )
          """)
  List<Trainer> findActiveTrainersNotAssignedToTrainee(@Param("username") String traineeUsername);

  @Query(
      """
          SELECT DISTINCT t
          FROM Trainer t
          LEFT JOIN FETCH t.user user
          WHERE LOWER(user.username) IN :usernames
          """)
  List<Trainer> findAllByUsernames(@Param("usernames") List<String> usernames);
}
