package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.AuthenticationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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
  private TraineeServiceImpl traineeService;

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
  void updateTraineeShouldSaveMappedEntityWithRequestedId() {
    Long traineeId = 10L;
    TraineeDto traineeDto = createTraineeDto(null, "updated.trainee");
    Trainee existingTrainee = createTrainee(traineeId, "old.trainee");
    Trainee mappedTrainee = createTrainee(null, "updated.trainee");
    Trainee savedTrainee = createTrainee(traineeId, "updated.trainee");
    TraineeDto savedTraineeDto = createTraineeDto(traineeId, "updated.trainee");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
    when(modelMapper.map(traineeDto, Trainee.class)).thenReturn(mappedTrainee);
    when(traineeRepository.save(mappedTrainee)).thenReturn(savedTrainee);
    when(modelMapper.map(savedTrainee, TraineeDto.class)).thenReturn(savedTraineeDto);

    TraineeDto result = traineeService.updateTrainee(traineeId, traineeDto);

    Assertions.assertThat(result).isSameAs(savedTraineeDto);
    Assertions.assertThat(mappedTrainee.getId()).isEqualTo(traineeId);
    verify(traineeRepository).save(mappedTrainee);
  }

  @Test
  void updateTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;
    TraineeDto traineeDto = createTraineeDto(null, "missing.trainee");

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
}

