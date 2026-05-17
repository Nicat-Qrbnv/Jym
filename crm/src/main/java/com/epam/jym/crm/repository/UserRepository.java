package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  @Query(
      """
          SELECT COUNT (*)
          FROM User u
          WHERE u.username LIKE CONCAT(:firstName, '.', :lastName, '%')
          """)
  Integer findNumberOfUsersWithSameName(String username);
}
