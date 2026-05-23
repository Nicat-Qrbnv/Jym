package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.BadCredentialsException;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;

  @Override
  public void authenticate(CredentialsDto credentials) {
    if (credentials == null
        || !StringUtils.hasText(credentials.username())
        || !StringUtils.hasText(credentials.password())) {
      throw failAuthentication(credentials == null ? "null" : credentials.username());
    }

    User foundUser = userRepository.findByUsername(credentials.username())
        .orElseThrow(() -> failAuthentication(credentials.username()));

    boolean passwordMatches = credentials.password().equals(foundUser.getPassword());

    if (!passwordMatches || !foundUser.isActive()) {
      throw failAuthentication(credentials.username());
    }

    log.info("Authentication successful for username= {}", credentials.username());
  }

  private BadCredentialsException failAuthentication(String username) {
    log.warn("Authentication failed for user= {}", username);
    return new BadCredentialsException("Invalid username or password");
  }
}
