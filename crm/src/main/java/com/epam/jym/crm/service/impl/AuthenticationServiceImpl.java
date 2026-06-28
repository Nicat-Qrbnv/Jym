package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidCredentialsException;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.BruteForceProtectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final BruteForceProtectionService bruteForceProtectionService;

  @Override
  public void authenticate(CredentialsDto credentials) {
    if (credentials == null
        || !StringUtils.hasText(credentials.username())
        || !StringUtils.hasText(credentials.password())) {
      throw failAuthentication(credentials == null ? "null" : credentials.username());
    }
    if (bruteForceProtectionService.isBlocked(credentials.username())) {
      throw failBlockedAuthentication(credentials.username());
    }

    User foundUser =
        userRepository
            .findByUsername(credentials.username())
            .orElseThrow(() -> failAuthentication(credentials.username()));

    boolean passwordMatches =
        passwordEncoder.matches(credentials.password(), foundUser.getPassword());

    if (!passwordMatches || !foundUser.isActive()) {
      bruteForceProtectionService.registerFailure(credentials.username());
      throw failAuthentication(credentials.username());
    }

    bruteForceProtectionService.registerSuccess(credentials.username());
    log.info("Authentication successful for username= {}", credentials.username());
  }

  private InvalidCredentialsException failAuthentication(String username) {
    log.warn("Authentication failed for user= {}", username);
    return new InvalidCredentialsException("Invalid username or password");
  }

  private InvalidCredentialsException failBlockedAuthentication(String username) {
    log.warn("Authentication blocked for user= {}", username);
    return new InvalidCredentialsException("User is temporarily blocked");
  }
}
