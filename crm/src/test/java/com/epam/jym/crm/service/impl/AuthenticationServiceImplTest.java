package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.entity.User;
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
  void authenticateShouldReturnAuthenticatedUserWhenCredentialsMatchActiveUser() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    User user = createUser(true);
    user.setId(10L);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

    authenticationService.authenticate(credentials);

    verify(userRepository).findByUsername("john.doe");
  }

  @Test
  void authenticateShouldThrowWhenPasswordDoesNotMatch() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "wrong-password");
    User user = createUser(true);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

    Assertions.assertThatThrownBy(() -> authenticationService.authenticate(credentials))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid username or password");
  }

  @Test
  void authenticateShouldThrowWhenUserIsInactive() {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    User user = createUser(false);

    when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

    Assertions.assertThatThrownBy(() -> authenticationService.authenticate(credentials))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid username or password");
  }

  @Test
  void authenticateShouldThrowWhenCredentialsAreMissing() {
    Assertions.assertThatThrownBy(() -> authenticationService.authenticate(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Credentials are required");
  }

  private User createUser(boolean active) {
    User user = new User();
    user.setUsername("john.doe");
    user.setPassword("password");
    user.setActive(active);
    return user;
  }
}
