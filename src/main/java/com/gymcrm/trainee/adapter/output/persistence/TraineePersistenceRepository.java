package com.gymcrm.trainee.adapter.output.persistence;

import com.gymcrm.trainee.domain.Trainee;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TraineePersistenceRepository extends JpaRepository<Trainee, UUID> {
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
