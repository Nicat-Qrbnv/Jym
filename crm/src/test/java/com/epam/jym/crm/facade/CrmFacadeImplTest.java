package com.epam.jym.crm.facade;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.dto.TraineeUpdateDto;
import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainerUpdateDto;
import com.epam.jym.crm.dto.TrainingDto;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrmFacadeImplTest {

  @Mock
  private TraineeService traineeService;

  @Mock
  private TrainerService trainerService;

  @Mock
  private TrainingService trainingService;

  private CrmFacade crmFacade;

  @BeforeEach
  void setUp() {
    crmFacade = new CrmFacadeImpl(traineeService, trainerService, trainingService);
  }

  @Test
  void traineeMethodsShouldDelegateToTraineeService() {
    Long traineeId = 1L;
    String username = "John.Smith";
    TraineeDto traineeDto = createTraineeDto(traineeId, username);
    TraineeUpdateDto traineeUpdateDto = createTraineeUpdateDto();
    List<TraineeDto> trainees = List.of(traineeDto);

    when(traineeService.createTrainee(traineeDto)).thenReturn(traineeDto);
    when(traineeService.updateTrainee(traineeId, traineeUpdateDto)).thenReturn(traineeDto);
    when(traineeService.selectTrainee(traineeId)).thenReturn(Optional.of(traineeDto));
    when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.of(traineeDto));
    when(traineeService.selectAllTrainees()).thenReturn(trainees);

    Assertions.assertThat(crmFacade.createTrainee(traineeDto)).isSameAs(traineeDto);
    Assertions.assertThat(crmFacade.updateTrainee(traineeId, traineeUpdateDto)).isSameAs(traineeDto);
    crmFacade.deleteTrainee(traineeId);
    Assertions.assertThat(crmFacade.selectTrainee(traineeId)).containsSame(traineeDto);
    Assertions.assertThat(crmFacade.selectTraineeByUsername(username)).containsSame(traineeDto);
    Assertions.assertThat(crmFacade.selectAllTrainees()).isSameAs(trainees);

    verify(traineeService).createTrainee(traineeDto);
    verify(traineeService).updateTrainee(traineeId, traineeUpdateDto);
    verify(traineeService).deleteTrainee(traineeId);
    verify(traineeService).selectTrainee(traineeId);
    verify(traineeService).selectTraineeByUsername(username);
    verify(traineeService).selectAllTrainees();
  }

  @Test
  void trainerMethodsShouldDelegateToTrainerService() {
    Long trainerId = 2L;
    String username = "Jane.Coach";
    TrainerDto trainerDto = createTrainerDto(trainerId, username);
    TrainerUpdateDto trainerUpdateDto = createTrainerUpdateDto();
    List<TrainerDto> trainers = List.of(trainerDto);

    when(trainerService.createTrainer(trainerDto)).thenReturn(trainerDto);
    when(trainerService.updateTrainer(trainerId, trainerUpdateDto)).thenReturn(trainerDto);
    when(trainerService.selectTrainer(trainerId)).thenReturn(Optional.of(trainerDto));
    when(trainerService.selectTrainerByUsername(username)).thenReturn(Optional.of(trainerDto));
    when(trainerService.selectAllTrainers()).thenReturn(trainers);

    Assertions.assertThat(crmFacade.createTrainer(trainerDto)).isSameAs(trainerDto);
    Assertions.assertThat(crmFacade.updateTrainer(trainerId, trainerUpdateDto)).isSameAs(trainerDto);
    Assertions.assertThat(crmFacade.selectTrainer(trainerId)).containsSame(trainerDto);
    Assertions.assertThat(crmFacade.selectTrainerByUsername(username)).containsSame(trainerDto);
    Assertions.assertThat(crmFacade.selectAllTrainers()).isSameAs(trainers);

    verify(trainerService).createTrainer(trainerDto);
    verify(trainerService).updateTrainer(trainerId, trainerUpdateDto);
    verify(trainerService).selectTrainer(trainerId);
    verify(trainerService).selectTrainerByUsername(username);
    verify(trainerService).selectAllTrainers();
  }

  @Test
  void trainingMethodsShouldDelegateToTrainingService() {
    Long trainingId = 3L;
    TrainingDto trainingDto = createTrainingDto(trainingId);
    List<TrainingDto> trainings = List.of(trainingDto);

    when(trainingService.createTraining(trainingDto)).thenReturn(trainingDto);
    when(trainingService.selectTraining(trainingId)).thenReturn(Optional.of(trainingDto));
    when(trainingService.selectAllTrainings()).thenReturn(trainings);

    Assertions.assertThat(crmFacade.createTraining(trainingDto)).isSameAs(trainingDto);
    Assertions.assertThat(crmFacade.selectTraining(trainingId)).containsSame(trainingDto);
    Assertions.assertThat(crmFacade.selectAllTrainings()).isSameAs(trainings);

    verify(trainingService).createTraining(trainingDto);
    verify(trainingService).selectTraining(trainingId);
    verify(trainingService).selectAllTrainings();
  }

  private TraineeDto createTraineeDto(Long id, String username) {
    return new TraineeDto(
        id,
        "John",
        "Smith",
        username,
        "password123",
        true,
        LocalDate.of(1995, 1, 1),
        "Baku");
  }

  private TraineeUpdateDto createTraineeUpdateDto() {
    return new TraineeUpdateDto(
        "John",
        "Smith",
        "password123",
        true,
        LocalDate.of(1995, 1, 1),
        "Baku");
  }

  private TrainerDto createTrainerDto(Long id, String username) {
    return new TrainerDto(
        id,
        "Jane",
        "Coach",
        username,
        "password123",
        true,
        null,
        null);
  }

  private TrainerUpdateDto createTrainerUpdateDto() {
    return new TrainerUpdateDto(
        "Jane",
        "Coach",
        "password123",
        true,
        null,
        null);
  }

  private TrainingDto createTrainingDto(Long id) {
    return new TrainingDto(
        id,
        "Morning training",
        null,
        null,
        null,
        LocalDate.of(2026, 5, 9),
        60);
  }
}

