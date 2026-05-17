package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.RegisteredUserDto;
import com.epam.jym.crm.dto.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.util.PasswordGeneratorUtil;
import jakarta.transaction.Transactional;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final ReentrantLock lock = new ReentrantLock();
  private final UserRepository userRepo;
  private final ModelMapper mapper;

  @Value(value = "${security.password.length}")
  private int passwordLength;

  @Transactional
  @Override
  public RegisteredUserDto register(UserCreateDto userDto) {
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
      log.info("Registered user: {}", user);
    } finally {
      lock.unlock();
    }
    return mapper.map(user, RegisteredUserDto.class);
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
}
