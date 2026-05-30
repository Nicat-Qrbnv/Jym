package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(
      """
          SELECT COUNT (*)
          FROM User u
          WHERE u.username LIKE :username
          """)
  Integer findNumberOfUsersWithSameName(String username);

  Optional<User> findByUsername(String username);

  @Modifying
  @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
  int changePassword(String username, String newPassword);

  @Modifying
  @Query(
      """
          UPDATE User u
          SET u.isActive = CASE WHEN u.isActive THEN FALSE ELSE TRUE END
          WHERE u.username = :username
          """)
  int changeStatus(String username);

  @Modifying
  @Query(
      """
          UPDATE User u
          SET u.isActive = :isActive
          WHERE u.id = :userId
          """)
  void changeStatus(Long userId, Boolean isActive);
}
