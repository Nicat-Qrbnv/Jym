package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.UserRepository;
import java.util.Optional;
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
    UserCreateDto userDto = new UserCreateDto("John", "Doe");

    when(userRepository.findNumberOfUsersWithSameName("John.Doe%")).thenReturn(0);
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(10L);
              return user;
            });

    User registeredUser = authenticationService.register(userDto);

    Assertions.assertThat(registeredUser.getId()).isEqualTo(10L);
    Assertions.assertThat(registeredUser.getFirstName()).isEqualTo("John");
    Assertions.assertThat(registeredUser.getLastName()).isEqualTo("Doe");
    Assertions.assertThat(registeredUser.getUsername()).isEqualTo("John.Doe");
    Assertions.assertThat(registeredUser.getPassword()).hasSize(PASSWORD_LENGTH);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    Assertions.assertThat(savedUser.getFirstName()).isEqualTo("John");
    Assertions.assertThat(savedUser.getLastName()).isEqualTo("Doe");
    Assertions.assertThat(savedUser.getUsername()).isEqualTo("John.Doe");
    Assertions.assertThat(savedUser.getPassword()).hasSize(PASSWORD_LENGTH);
    Assertions.assertThat(savedUser.isActive()).isTrue();
  }

  @Test
  void registerShouldAddSuffixWhenUsernameAlreadyExists() {
    UserCreateDto userDto = new UserCreateDto("John", "Doe");

    when(userRepository.findNumberOfUsersWithSameName("John.Doe%")).thenReturn(2);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User registeredUser = authenticationService.register(userDto);

    Assertions.assertThat(registeredUser.getUsername()).isEqualTo("John.Doe2");
    Assertions.assertThat(registeredUser.getPassword()).hasSize(PASSWORD_LENGTH);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void registerShouldThrowExceptionWhenUserIsNull() {
    Assertions.assertThatThrownBy(() -> authenticationService.register(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("user must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenFirstNameIsNull() {
    UserCreateDto userDto = new UserCreateDto(null, "Doe");

    Assertions.assertThatThrownBy(() -> authenticationService.register(userDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenLastNameIsNull() {
    UserCreateDto userDto = new UserCreateDto("John", null);

    Assertions.assertThatThrownBy(() -> authenticationService.register(userDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository);
  }

  @Test
  void changePasswordShouldDelegateToRepository() {
    authenticationService.changePassword(10L, "newPassword");

    verify(userRepository).changePassword(10L, "newPassword");
  }

  @Test
  void changeUserStatusShouldDelegateToRepository() {
    authenticationService.changeUserStatus(10L);

    verify(userRepository).changeStatus(10L);
  }

  @Test
  void getUserShouldReturnUserWhenExists() {
    User user = createUser();

    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    User result = authenticationService.getUser(10L);

    Assertions.assertThat(result).isSameAs(user);
  }

  @Test
  void getUserShouldThrowExceptionWhenUserDoesNotExist() {
    when(userRepository.findById(404L)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> authenticationService.getUser(404L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User not found: 404");
  }

  private User createUser() {
    User user = new User();
    user.setId(10L);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setUsername("John.Doe");
    user.setPassword("password");
    user.setActive(true);
    return user;
  }

}
