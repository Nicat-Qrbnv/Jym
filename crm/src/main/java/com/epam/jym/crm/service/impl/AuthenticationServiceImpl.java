package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;

  @Override
  public void authenticate(CredentialsDto credentials) {
    if (credentials == null || credentials.username() == null || credentials.password() == null) {
      throw new IllegalArgumentException("Credentials are required");
    }
    userRepository
        .findByUsername(credentials.username())
        .ifPresent(
            foundUser -> {
              boolean rightPassword = foundUser.getPassword().equals(credentials.password());
              if (rightPassword && foundUser.isActive()) {
                log.info("Authentication successful for username = {}", credentials.username());
              } else {
                log.warn("Authentication failed for username={}", credentials.username());
                throw new IllegalArgumentException("Invalid username or password");
              }
            });
  }
}
