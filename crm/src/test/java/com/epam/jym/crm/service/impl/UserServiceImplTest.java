package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidRequestException;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private static final int PASSWORD_LENGTH = 10;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceImpl authenticationService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(authenticationService, "passwordLength", PASSWORD_LENGTH);
  }

  @Test
  void registerShouldCreateUserSaveAndReturnRegisteredUser() {
    UserCreateDto profile = new UserCreateDto("John", "Doe");

    when(userRepository.findNumberOfUsersWithSameName("^john\\.doe[0-9]*$")).thenReturn(0L);

    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(10L);
              return user;
            });

    User registeredUser = authenticationService.register(profile);

    Assertions.assertThat(registeredUser.getId()).isEqualTo(10L);
    Assertions.assertThat(registeredUser.getFirstName()).isEqualTo("John");
    Assertions.assertThat(registeredUser.getLastName()).isEqualTo("Doe");
    Assertions.assertThat(registeredUser.getUsername()).isEqualTo("john.doe");
    Assertions.assertThat(registeredUser.getPassword()).hasSize(PASSWORD_LENGTH);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    Assertions.assertThat(savedUser.getFirstName()).isEqualTo("John");
    Assertions.assertThat(savedUser.getLastName()).isEqualTo("Doe");
    Assertions.assertThat(savedUser.getUsername()).isEqualTo("john.doe");
    Assertions.assertThat(savedUser.getPassword()).hasSize(PASSWORD_LENGTH);
    Assertions.assertThat(savedUser.isActive()).isTrue();
  }

  @Test
  void registerShouldAddSuffixWhenUsernameAlreadyExists() {
    UserCreateDto profile = new UserCreateDto("John", "Doe");

    when(userRepository.findNumberOfUsersWithSameName("^john\\.doe[0-9]*$")).thenReturn(2L);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User registeredUser = authenticationService.register(profile);

    Assertions.assertThat(registeredUser.getUsername()).isEqualTo("john.doe2");
    Assertions.assertThat(registeredUser.getPassword()).hasSize(PASSWORD_LENGTH);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void registerShouldThrowExceptionWhenUserIsNull() {
    Assertions.assertThatThrownBy(() -> authenticationService.register(null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("profile must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenFirstNameIsNull() {
    UserCreateDto profile = new UserCreateDto(null, "Doe");

    Assertions.assertThatThrownBy(() -> authenticationService.register(profile))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenLastNameIsNull() {
    UserCreateDto profile = new UserCreateDto("John", null);

    Assertions.assertThatThrownBy(() -> authenticationService.register(profile))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void changePasswordShouldDelegateToRepository() {
    when(userRepository.changePassword("john.doe", "newPassword")).thenReturn(1);

    authenticationService.changePassword("john.doe", "newPassword");

    verify(userRepository).changePassword("john.doe", "newPassword");
  }

  @Test
  void changePasswordShouldThrowExceptionWhenUserDoesNotExist() {
    when(userRepository.changePassword("missing", "newPassword")).thenReturn(0);

    Assertions.assertThatThrownBy(
            () -> authenticationService.changePassword("missing", "newPassword"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found: missing");
  }

  @Test
  void changeUserStatusShouldDelegateToRepository() {
    authenticationService.changeUserStatus("profile.name1", false);

    verify(userRepository, times(0)).changeStatus("profile.name1");
  }

  @Test
  void changeUserStatusShouldThrowExceptionWhenUserDoesNotExist() {
    when(userRepository.changeStatus("missing")).thenReturn(0);

    Assertions.assertThatThrownBy(() -> authenticationService.changeUserStatus("missing", true))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found: missing");
    verify(userRepository, never()).changeStatus(0L, false);
  }
}
