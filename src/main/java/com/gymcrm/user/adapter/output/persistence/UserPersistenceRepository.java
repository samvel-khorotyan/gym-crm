package com.gymcrm.user.adapter.output.persistence;

import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPersistenceRepository extends JpaRepository<User, UUID> {
  @Query("SELECT DISTINCT u.username FROM User u WHERE u.username LIKE :baseUsername%")
  List<String> findDistinctUsernamesStartingWith(@Param("baseUsername") String baseUsername);

  boolean existsByUsernameAndPasswordAndUserType(
      String username, String password, UserType userType);

  Optional<User> findByUsername(String username);
}
