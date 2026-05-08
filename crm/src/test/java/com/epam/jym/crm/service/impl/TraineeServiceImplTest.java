package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.dto.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.TraineeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TraineeServiceImpl traineeServiceImpl;

  private TraineeService traineeService;

  @BeforeEach
  void setUp() {
    traineeService = traineeServiceImpl;
  }

  @Test
  void createTraineeShouldRegisterSaveAndReturnDto() {
    TraineeDto traineeDto = createTraineeDto(1L, "john.doe");
    Trainee traineeEntity = createTrainee(null, null);
    Trainee registeredTrainee = createTrainee(10L, "john.doe");
    TraineeDto savedTraineeDto = createTraineeDto(10L, "john.doe");

    when(modelMapper.map(traineeDto, Trainee.class)).thenReturn(traineeEntity);
    when(authenticationService.register(traineeEntity)).thenReturn(registeredTrainee);
    when(traineeRepository.save(registeredTrainee)).thenReturn(registeredTrainee);
    when(modelMapper.map(registeredTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.createTrainee(traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    verify(authenticationService).register(traineeEntity);
    verify(traineeRepository).save(registeredTrainee);
  }

  @Test
  void updateTraineeShouldPreserveIdAndRegenerateUsernameWhenNameChanges() {
    Long traineeId = 10L;
    TraineeUpdateDto traineeDto = createTraineeUpdateDto();
    Trainee existingTrainee = createTrainee(traineeId, "Old.Name");
    existingTrainee.setFirstName("Old");
    existingTrainee.setLastName("Name");
    TraineeDto savedTraineeDto = createTraineeDto(traineeId, "John.Doe");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
    when(authenticationService.generateUsername("John", "Doe")).thenReturn("John.Doe");
    when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);
    when(modelMapper.map(existingTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.updateTrainee(traineeId, traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    Assertions.assertThat(existingTrainee.getId()).isEqualTo(traineeId);
    Assertions.assertThat(existingTrainee.getUsername()).isEqualTo("John.Doe");
    verify(authenticationService).generateUsername("John", "Doe");
    verify(traineeRepository).save(existingTrainee);
  }

  @Test
  void updateTraineeShouldKeepUsernameWhenNameDoesNotChange() {
    Long traineeId = 10L;
    TraineeUpdateDto traineeDto = createTraineeUpdateDto();
    Trainee existingTrainee = createTrainee(traineeId, "John.Doe");
    TraineeDto savedTraineeDto = createTraineeDto(traineeId, "John.Doe");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
    when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);
    when(modelMapper.map(existingTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.updateTrainee(traineeId, traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    Assertions.assertThat(existingTrainee.getUsername()).isEqualTo("John.Doe");
    verifyNoInteractions(authenticationService);
  }

  @Test
  void updateTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;
    TraineeUpdateDto traineeDto = createTraineeUpdateDto();

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.updateTrainee(traineeId, traineeDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);

    verifyNoInteractions(authenticationService, modelMapper);
  }

  @Test
  void deleteTraineeShouldDeleteById() {
    Long traineeId = 10L;

    traineeService.deleteTrainee(traineeId);

    verify(traineeRepository).delete(traineeId);
  }

  @Test
  void selectTraineeShouldReturnMappedDtoWhenTraineeExists() {
    Long traineeId = 10L;
    Trainee trainee = createTrainee(traineeId, "john.doe");
    TraineeDto traineeDto = createTraineeDto(traineeId, "john.doe");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
    when(modelMapper.map(trainee, TraineeDto.class)).thenReturn(traineeDto);

    Optional<TraineeDto> result = traineeService.selectTrainee(traineeId);

    Assertions.assertThat(result).contains(traineeDto);
  }

  @Test
  void selectTraineeShouldReturnEmptyWhenTraineeDoesNotExist() {
    Long traineeId = 404L;

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Optional<TraineeDto> result = traineeService.selectTrainee(traineeId);

    Assertions.assertThat(result).isEmpty();
    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectTraineeByUsernameShouldReturnMappedDtoWhenTraineeExists() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, username);
    TraineeDto traineeDto = createTraineeDto(10L, username);

    when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
    when(modelMapper.map(trainee, TraineeDto.class)).thenReturn(traineeDto);

    Optional<TraineeDto> result = traineeService.selectTraineeByUsername(username);

    Assertions.assertThat(result).contains(traineeDto);
  }

  @Test
  void selectAllTraineesShouldReturnMappedDtos() {
    Trainee firstTrainee = createTrainee(1L, "first.trainee");
    Trainee secondTrainee = createTrainee(2L, "second.trainee");
    TraineeDto firstDto = createTraineeDto(1L, "first.trainee");
    TraineeDto secondDto = createTraineeDto(2L, "second.trainee");

    when(traineeRepository.findAll()).thenReturn(List.of(firstTrainee, secondTrainee));
    when(modelMapper.map(firstTrainee, TraineeDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTrainee, TraineeDto.class)).thenReturn(secondDto);

    List<TraineeDto> result = traineeService.selectAllTrainees();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  private Trainee createTrainee(Long id, String username) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setFirstName("John");
    trainee.setLastName("Doe");
    trainee.setUsername(username);
    trainee.setPassword("password");
    trainee.setActive(true);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
  }

  private TraineeDto createTraineeDto(Long id, String username) {
    return new TraineeDto(
        id,
        "John",
        "Doe",
        username,
        "password",
        true,
        LocalDate.of(2000, 1, 1),
        "Baku");
  }

  private TraineeUpdateDto createTraineeUpdateDto() {
    return new TraineeUpdateDto(
        "John",
        "Doe",
        "password",
        true,
        LocalDate.of(2000, 1, 1),
        "Baku");
  }
}

