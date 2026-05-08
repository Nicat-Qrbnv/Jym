package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.AuthenticationService;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private static final int PASSWORD_LENGTH = 10;
  private static final AtomicLong USER_ID_SEQUENCE = new AtomicLong(1_000L);

  private final TrainerRepository trainerRepository;
  private final TraineeRepository traineeRepository;

  @Override
  public <U extends User> U register(U user) {
    if (user == null) {
      log.warn("Registration failed: user is null");
      throw new IllegalArgumentException("user must not be null");
    }

    user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
    user.setPassword(generatePassword());
    if (user.getId() == null) {
      user.setId(USER_ID_SEQUENCE.incrementAndGet());
    }
    log.info(
        "Registered user type={} id={} username={}",
        user.getClass().getSimpleName(),
        user.getId(),
        user.getUsername());
    return user;
  }

  private String generateUsername(String firstName, String lastName) {
    if (firstName == null || lastName == null) {
      log.warn("Username generation failed: firstName or lastName is null");
      throw new IllegalArgumentException("firstName and lastName must not be null");
    }

    String baseUsername = (firstName + "." + lastName).toLowerCase(Locale.ROOT);
    if (usernameExists(baseUsername)) {
      log.debug("Username {} already exists, adding suffix", baseUsername);
      baseUsername += countUsersWithSameName(baseUsername);
    }

    log.debug("Generated username {}", baseUsername);
    return baseUsername;
  }

  private boolean usernameExists(String baseUsername) {
    Predicate<UserRepository<? extends User>> exists =
        repo -> repo.findByUsername(baseUsername).isPresent();
    return exists.test(trainerRepository) || exists.test(traineeRepository);
  }

  private String generatePassword() {
    return UUID.randomUUID().toString().substring(0, PASSWORD_LENGTH);
  }

  private long countUsersWithSameName(String newUsername) {
    Function<UserRepository<? extends User>, Long> countUsers =
        repository ->
            repository.findAll().stream()
                .map(u -> u.getFirstName() + "." + u.getLastName())
                .filter(existingUsername -> existingUsername.equalsIgnoreCase(newUsername))
                .count();

    return countUsers.apply(trainerRepository) + countUsers.apply(traineeRepository);
  }
}
