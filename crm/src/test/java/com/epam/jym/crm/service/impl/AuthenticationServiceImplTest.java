package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidCredentialsException;
import com.epam.jym.crm.repository.UserRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthenticationServiceImpl authenticationService;

  @Test
  void authenticateShouldSucceedWhenCredentialsAreValid() {
    User profile = createUser(true);
    profile.setId(10L);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(profile));
    when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

    CredentialsDto credentials = new CredentialsDto("john.doe", "password");

    Assertions.assertThatCode(() -> authenticationService.authenticate(credentials))
        .doesNotThrowAnyException();
  }

  @Test
  void authenticateShouldThrowWhenCredentialsAreEmpty() {
    CredentialsDto credentials = new CredentialsDto("", "");

    assertBadCredentials(credentials);
    verifyNoInteractions(userRepository);
  }

  @Test
  void authenticateShouldThrowWhenUsernameIsEmpty() {
    CredentialsDto credentials = new CredentialsDto("", "password");

    assertBadCredentials(credentials);
    verifyNoInteractions(userRepository);
  }

  @Test
  void authenticateShouldThrowWhenPasswordIsEmpty() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "");

    assertBadCredentials(credentials);
    verifyNoInteractions(userRepository);
  }

  @Test
  void authenticateShouldThrowWhenUsernameHasTypo() {
    CredentialsDto credentials = new CredentialsDto("john.do", "password");

    when(userRepository.findByUsername("john.do")).thenReturn(Optional.empty());

    assertBadCredentials(credentials);
  }

  @Test
  void authenticateShouldThrowWhenPasswordHasTypo() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "passwrod");
    User profile = createUser(true);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(profile));
    when(passwordEncoder.matches("passwrod", "encodedPassword")).thenReturn(false);

    assertBadCredentials(credentials);
  }

  @Test
  void authenticateShouldThrowWhenUserIsInactive() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    User profile = createUser(false);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(profile));

    assertBadCredentials(credentials);
  }

  private void assertBadCredentials(CredentialsDto credentials) {
    Assertions.assertThatThrownBy(() -> authenticationService.authenticate(credentials))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessage("Invalid username or password");
  }

  private User createUser(boolean active) {
    User profile = new User();
    profile.setUsername("john.doe");
    profile.setPassword("encodedPassword");
    profile.setActive(active);
    return profile;
  }
}
