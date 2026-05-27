package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.UserService;
import com.epam.jym.crm.util.PasswordGeneratorUtil;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final ReentrantLock lock = new ReentrantLock();
  private final UserRepository userRepo;

  @Value(value = "${security.password.length}")
  private int passwordLength;

  @Transactional
  @Override
  public User register(UserCreateDto userDto) {
    if (userDto == null) {
      log.warn("Registration failed: user is null");
      throw new IllegalArgumentException("user must not be null");
    }

    User user = new User();
    try {
      lock.lock();
      user.setFirstName(userDto.firstName());
      user.setLastName(userDto.lastName());
      user.setUsername(generateUsername(userDto.firstName(), userDto.lastName()));
      user.setPassword(generatePassword());
      user.setActive(true);
      user = userRepo.save(user);
    } finally {
      lock.unlock();
    }
    return user;
  }

  private String generateUsername(String firstName, String lastName) {
    if (firstName == null || lastName == null) {
      log.warn("Username generation failed: firstName or lastName is null");
      throw new IllegalArgumentException("firstName and lastName must not be null");
    }

    String baseUsername = firstName + "." + lastName;

    Integer duplicates = userRepo.findNumberOfUsersWithSameName(baseUsername + '%');
    log.debug("Found {} users with the same name", duplicates);
    return duplicates > 0 ? baseUsername + duplicates : baseUsername;
  }

  private String generatePassword() {
    return PasswordGeneratorUtil.generateRandomPassword(passwordLength);
  }

  @Transactional
  @Override
  public void changePassword(Long userId, String newPassword) {
    userRepo.changePassword(userId, newPassword);
  }

  @Transactional
  @Override
  public void changeUserStatus(Long userId) {
    userRepo.changeStatus(userId);
  }

  @Override
  public User getUser(Long userId) {
    return userRepo
        .findById(userId)
        .orElseThrow(
            () -> {
              log.warn("Failed to create trainer: userId={} not found", userId);
              return new IllegalArgumentException("User not found: " + userId);
            });
  }
}
