package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

  @Mock private TraineeRepository traineeRepository;

  @Mock private UserService userService;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private TraineeServiceImpl traineeServiceImpl;

  private TraineeService traineeService;

  @BeforeEach
  public void setUp() {
    traineeServiceImpl.setMapper(modelMapper);
    traineeService = traineeServiceImpl;
  }

  @Test
  void createTraineeShouldLoadUserSaveProfileAndReturnDto() {
    TraineeCreateDto traineeDto = createTraineeCreateDto();
    User user = createUser(1L, "john.doe");
    Trainee savedTrainee = createTrainee(10L, user);
    TraineeDto savedTraineeDto = createTraineeDto(10L, "john.doe");

    when(traineeRepository.userHasTraineeProfile(1L)).thenReturn(false);
    when(userService.getUser(1L)).thenReturn(user);
    when(traineeRepository.save(any(Trainee.class))).thenReturn(savedTrainee);
    when(modelMapper.map(savedTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.createTrainee(traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
    verify(traineeRepository).save(traineeCaptor.capture());
    Assertions.assertThat(traineeCaptor.getValue().getUser()).isSameAs(user);
    Assertions.assertThat(traineeCaptor.getValue().getDateOfBirth())
        .isEqualTo(traineeDto.dateOfBirth());
    Assertions.assertThat(traineeCaptor.getValue().getAddress()).isEqualTo(traineeDto.address());
  }

  @Test
  void createTraineeShouldThrowExceptionWhenUserAlreadyHasTraineeProfile() {
    TraineeCreateDto traineeDto = createTraineeCreateDto();

    when(traineeRepository.userHasTraineeProfile(1L)).thenReturn(true);

    Assertions.assertThatThrownBy(() -> traineeService.createTrainee(traineeDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User already has a trainee profile: 1");

    verifyNoInteractions(userService, modelMapper);
  }

  @Test
  void updateTraineeShouldUpdateBirthDateAndAddress() {
    Long traineeId = 10L;
    final TraineeUpdateDto traineeDto = createTraineeUpdateDto();
    Trainee existingTrainee = createTrainee(traineeId, createUser(1L, "john.doe"));
    existingTrainee.setDateOfBirth(LocalDate.of(1999, 12, 31));
    existingTrainee.setAddress("Old address");
    TraineeDto savedTraineeDto = createTraineeDto(traineeId, "John.Doe");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
    when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);
    when(modelMapper.map(existingTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.updateTrainee(traineeId, traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    Assertions.assertThat(existingTrainee.getDateOfBirth()).isEqualTo(traineeDto.dateOfBirth());
    Assertions.assertThat(existingTrainee.getAddress()).isEqualTo(traineeDto.address());
    verify(traineeRepository).save(existingTrainee);
  }

  @Test
  void updateTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;
    TraineeUpdateDto traineeDto = createTraineeUpdateDto();

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.updateTrainee(traineeId, traineeDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);

    verifyNoInteractions(userService, modelMapper);
  }

  @Test
  void deleteTraineeShouldDeleteById() {
    Long traineeId = 10L;

    traineeService.deleteTrainee(traineeId);

    verify(traineeRepository).deleteById(traineeId);
  }

  @Test
  void deleteTraineeShouldDeleteByUsername() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, username));

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

    traineeService.deleteTrainee(username);

    verify(traineeRepository).delete(trainee);
  }

  @Test
  void deleteTraineeShouldThrowExceptionWhenUsernameDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.deleteTrainee(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + username);

    verifyNoInteractions(userService, modelMapper);
  }

  @Test
  void selectTraineeShouldReturnMappedDtoWhenTraineeExists() {
    Long traineeId = 10L;
    Trainee trainee = createTrainee(traineeId, createUser(1L, "john.doe"));
    TraineeDto traineeDto = createTraineeDto(traineeId, "john.doe");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
    when(modelMapper.map(trainee, TraineeDto.class)).thenReturn(traineeDto);

    TraineeDto result = traineeService.selectTrainee(traineeId);

    Assertions.assertThat(result).isSameAs(traineeDto);
  }

  @Test
  void selectTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.selectTrainee(traineeId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);

    verifyNoInteractions(modelMapper);
  }

  @Test
  void getTraineeByUsernameShouldReturnMappedDtoWhenTraineeExists() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, username));
    TraineeDto traineeDto = createTraineeDto(10L, username);

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
    when(modelMapper.map(trainee, TraineeDto.class)).thenReturn(traineeDto);

    TraineeDto result = traineeService.getTraineeByUsername(username);

    Assertions.assertThat(result).isSameAs(traineeDto);
  }

  @Test
  void getTraineeByUsernameShouldThrowExceptionWhenTraineeDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.getTraineeByUsername(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + username);

    verifyNoInteractions(modelMapper);
  }

  @Test
  void getAllTraineesShouldReturnMappedDtos() {
    Trainee firstTrainee = createTrainee(1L, createUser(1L, "first.trainee"));
    Trainee secondTrainee = createTrainee(2L, createUser(2L, "second.trainee"));
    TraineeDto firstDto = createTraineeDto(1L, "first.trainee");
    TraineeDto secondDto = createTraineeDto(2L, "second.trainee");

    when(traineeRepository.findAll()).thenReturn(List.of(firstTrainee, secondTrainee));
    when(modelMapper.map(firstTrainee, TraineeDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTrainee, TraineeDto.class)).thenReturn(secondDto);

    List<TraineeDto> result = traineeService.getAllTrainees();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  private Trainee createTrainee(Long id, User user) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setUser(user);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
  }

  private User createUser(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setUsername(username);
    user.setPassword("password");
    user.setActive(true);
    return user;
  }

  private TraineeDto createTraineeDto(Long id, String username) {
    return new TraineeDto(id, 1L, username, LocalDate.of(2000, 1, 1), "Baku");
  }

  private TraineeCreateDto createTraineeCreateDto() {
    return new TraineeCreateDto(1L, LocalDate.of(2000, 1, 1), "Baku");
  }

  private TraineeUpdateDto createTraineeUpdateDto() {
    return new TraineeUpdateDto(LocalDate.of(2000, 1, 1), "Baku");
  }
}
