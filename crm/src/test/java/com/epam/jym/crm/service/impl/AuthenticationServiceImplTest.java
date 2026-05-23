package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.BadCredentialsException;
import com.epam.jym.crm.repository.UserRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private AuthenticationServiceImpl authenticationService;

  @Test
  void authenticateShouldSucceedWhenCredentialsAreValid() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    User user = createUser(true);
    user.setId(10L);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

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
    User user = createUser(true);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

    assertBadCredentials(credentials);
  }

  @Test
  void authenticateShouldThrowWhenUserIsInactive() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    User user = createUser(false);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

    assertBadCredentials(credentials);
  }

  private void assertBadCredentials(CredentialsDto credentials) {
    Assertions.assertThatThrownBy(() -> authenticationService.authenticate(credentials))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid username or password");
  }

  private User createUser(boolean active) {
    User user = new User();
    user.setUsername("john.doe");
    user.setPassword("password");
    user.setActive(active);
    return user;
  }
}
