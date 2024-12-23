package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainerPersistenceRepository extends JpaRepository<Trainer, UUID> {
  @Query(
      """
                      SELECT t
                      FROM Trainer t
                      WHERE t.id NOT IN (
                          SELECT tr.trainer.id
                          FROM Training tr
                          WHERE tr.trainee.user.username = :username
                      )
                    """)
  List<Trainer> findTrainersNotAssignedToTrainee(@Param("username") String traineeUsername);
}
