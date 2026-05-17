package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.user.RegisteredUserDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private static final int PASSWORD_LENGTH = 10;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ModelMapper mapper;

  @InjectMocks
  private UserServiceImpl authenticationService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(authenticationService, "passwordLength", PASSWORD_LENGTH);
    authenticationService.setMapper(mapper);
  }

  @Test
  void registerShouldCreateUserSaveAndReturnRegisteredUser() {
    UserCreateDto userDto = new UserCreateDto("John", "Doe");

    when(userRepository.findNumberOfUsersWithSameName("John.Doe%")).thenReturn(0);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(10L);
      return user;
    });
    when(mapper.map(any(User.class), eq(RegisteredUserDto.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return toRegisteredUserDto(user);
    });

    RegisteredUserDto registeredUser = authenticationService.register(userDto);

    Assertions.assertThat(registeredUser.id()).isEqualTo(10L);
    Assertions.assertThat(registeredUser.firstName()).isEqualTo("John");
    Assertions.assertThat(registeredUser.lastName()).isEqualTo("Doe");
    Assertions.assertThat(registeredUser.username()).isEqualTo("John.Doe");
    Assertions.assertThat(registeredUser.password()).hasSize(PASSWORD_LENGTH);

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
    when(mapper.map(any(User.class), eq(RegisteredUserDto.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return toRegisteredUserDto(user);
    });

    RegisteredUserDto registeredUser = authenticationService.register(userDto);

    Assertions.assertThat(registeredUser.username()).isEqualTo("John.Doe2");
    Assertions.assertThat(registeredUser.password()).hasSize(PASSWORD_LENGTH);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void registerShouldThrowExceptionWhenUserIsNull() {
    Assertions.assertThatThrownBy(() -> authenticationService.register(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("user must not be null");

    verifyNoInteractions(userRepository, mapper);
  }

  @Test
  void registerShouldThrowExceptionWhenFirstNameIsNull() {
    UserCreateDto userDto = new UserCreateDto(null, "Doe");

    Assertions.assertThatThrownBy(() -> authenticationService.register(userDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository, mapper);
  }

  @Test
  void registerShouldThrowExceptionWhenLastNameIsNull() {
    UserCreateDto userDto = new UserCreateDto("John", null);

    Assertions.assertThatThrownBy(() -> authenticationService.register(userDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(userRepository, mapper);
  }

  private RegisteredUserDto toRegisteredUserDto(User user) {
    return new RegisteredUserDto(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getUsername(),
        user.getPassword());
  }
}
