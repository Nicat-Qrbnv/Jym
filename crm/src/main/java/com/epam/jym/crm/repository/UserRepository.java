package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.User;
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

  @Modifying
  @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
  void changePassword(Long userId, String newPassword);

  @Modifying
  @Query(
      """
          UPDATE User u
          SET u.isActive = CASE WHEN u.isActive THEN FALSE ELSE TRUE END
          WHERE u.id = :userId
          """)
  void changeStatus();
}
