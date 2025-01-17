package com.gymcrm.trainee.adapter.output.persistence;

import com.gymcrm.trainee.domain.Trainee;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TraineePersistenceRepository extends JpaRepository<Trainee, UUID> {
  Optional<Trainee> findByUserUsername(String username);

  @Query(
      """
          SELECT t
          FROM Trainee t
               LEFT JOIN FETCH t.trainers trainers
               JOIN FETCH t.user user
          WHERE user.username = :username
            AND (trainers IS NOT NULL OR trainers IS NULL)
          """)
  Optional<Trainee> findByUsernameWithTrainers(@Param("username") String username);

  @Query(
      """
          SELECT t
          FROM Trainee t
               LEFT JOIN FETCH t.trainers trainers
               JOIN FETCH t.user user
          WHERE t.id = :id
            AND (trainers IS NOT NULL OR trainers IS NULL)
          """)
  Optional<Trainee> findByIdWithTrainers(@Param("id") UUID id);

  @Modifying
  @Transactional
  @Query(
      """
          DELETE FROM Trainee t
          WHERE t.user.id IN (
              SELECT u.id
              FROM User u
              WHERE u.username = :username
          )
          """)
  void deleteByUsername(@Param("username") String username);
}
