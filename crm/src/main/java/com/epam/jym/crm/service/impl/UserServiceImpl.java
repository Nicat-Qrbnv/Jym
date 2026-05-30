package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidRequestException;
import com.epam.jym.crm.exception.ResourceNotFoundException;
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
      throw new InvalidRequestException("profile must not be null");
    }
    if (userDto.firstName() == null || userDto.lastName() == null) {
      log.warn("Registration failed: first name or last name is null");
      throw new InvalidRequestException("firstName and lastName must not be null");
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
  public void changePassword(String username, String newPassword) {
    int updatedRows = userRepo.changePassword(username, newPassword);
    if (updatedRows == 0) {
      throw new ResourceNotFoundException("User not found: " + username);
    }
  }

  @Transactional
  @Override
  public void changeUserStatus(String username) {
    int updatedRows = userRepo.changeStatus(username);
    if (updatedRows == 0) {
      throw new ResourceNotFoundException("User not found: " + username);
    }
  }

  @Transactional
  @Override
  public void deactivateUser(Long userId) {
    userRepo.changeStatus(userId, false);
  }
}
