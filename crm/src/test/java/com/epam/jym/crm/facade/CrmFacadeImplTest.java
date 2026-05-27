package com.epam.jym.crm.facade;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.util.mapper.TraineeMapper;
import com.epam.jym.crm.util.mapper.TrainerMapper;
import com.epam.jym.crm.util.mapper.TrainingMapper;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrmFacadeImplTest {

  private static final CredentialsDto CREDENTIALS = new CredentialsDto("john.doe", "password");

  @Mock private TraineeService traineeService;

  @Mock private TrainerService trainerService;

  @Mock private TrainingService trainingService;

  @Mock private TraineeMapper traineeMapper;

  @Mock private TrainerMapper trainerMapper;

  @Mock private TrainingMapper trainingMapper;

  @InjectMocks private CrmFacadeImpl crmFacade;

  @Test
  void createTraineeShouldReturnMappedCredentials() {
    TraineeCreateDto createDto = createTraineeCreateDto();
    Trainee trainee = createTrainee(1L, createUser(10L, "john.doe"));

    when(traineeService.createTrainee(createDto)).thenReturn(trainee);
    when(traineeMapper.toCredentialsDto(trainee)).thenReturn(CREDENTIALS);

    CredentialsDto result = crmFacade.createTrainee(createDto);

    Assertions.assertThat(result).isSameAs(CREDENTIALS);
  }

  @Test
  void updateTraineeShouldReturnMappedDto() {
    Long traineeId = 1L;
    TraineeUpdateDto updateDto = new TraineeUpdateDto(LocalDate.of(1990, 1, 1), "Main st");
    Trainee trainee = createTrainee(traineeId, createUser(10L, "john.doe"));
    TraineeDto traineeDto =
        new TraineeDto(traineeId, 10L, "john.doe", LocalDate.of(1990, 1, 1), "Main st");

    when(traineeService.updateTrainee(traineeId, updateDto)).thenReturn(trainee);
    when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

    TraineeDto result = crmFacade.updateTrainee(CREDENTIALS, traineeId, updateDto);

    Assertions.assertThat(result).isSameAs(traineeDto);
  }

  @Test
  void deleteTraineeShouldDelegateById() {
    crmFacade.deleteTrainee(CREDENTIALS, 1L);

    verify(traineeService).deleteTrainee(1L);
  }

  @Test
  void deleteTraineeShouldDelegateByUsername() {
    crmFacade.deleteTrainee(CREDENTIALS, "john.doe");

    verify(traineeService).deleteTrainee("john.doe");
  }

  @Test
  void getTraineeByIdShouldReturnMappedDto() {
    Trainee trainee = createTrainee(1L, createUser(10L, "john.doe"));
    TraineeDto traineeDto = new TraineeDto(1L, 10L, "john.doe", null, null);

    when(traineeService.getTrainee(1L)).thenReturn(trainee);
    when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

    TraineeDto result = crmFacade.getTraineeById(CREDENTIALS, 1L);

    Assertions.assertThat(result).isSameAs(traineeDto);
  }

  @Test
  void getTraineeByUsernameShouldReturnMappedDto() {
    Trainee trainee = createTrainee(1L, createUser(10L, "john.doe"));
    TraineeDto traineeDto = new TraineeDto(1L, 10L, "john.doe", null, null);

    when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
    when(traineeMapper.toTraineeDto(trainee)).thenReturn(traineeDto);

    TraineeDto result = crmFacade.getTraineeByUsername(CREDENTIALS, "john.doe");

    Assertions.assertThat(result).isSameAs(traineeDto);
  }

  @Test
  void getAllTraineesShouldReturnMappedDtos() {
    Trainee firstTrainee = createTrainee(1L, createUser(10L, "first.trainee"));
    Trainee secondTrainee = createTrainee(2L, createUser(20L, "second.trainee"));
    TraineeDto firstDto = new TraineeDto(1L, 10L, "first.trainee", null, null);
    TraineeDto secondDto = new TraineeDto(2L, 20L, "second.trainee", null, null);

    when(traineeService.getAllTrainees()).thenReturn(List.of(firstTrainee, secondTrainee));
    when(traineeMapper.toTraineeDto(firstTrainee)).thenReturn(firstDto);
    when(traineeMapper.toTraineeDto(secondTrainee)).thenReturn(secondDto);

    List<TraineeDto> result = crmFacade.getAllTrainees(CREDENTIALS);

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  @Test
  void updateTraineeTrainersShouldReturnMappedTrainerDtos() {
    List<Long> trainerIds = List.of(1L, 2L);
    List<Trainer> trainers =
        List.of(
            createTrainer(1L, createUser(10L, "first.trainer"), createTrainingType()),
            createTrainer(2L, createUser(20L, "second.trainer"), createTrainingType()));
    List<TrainerDto> trainerDtos =
        List.of(
            new TrainerDto(1L, 10L, "first.trainer", createTrainingTypeDto()),
            new TrainerDto(2L, 20L, "second.trainer", createTrainingTypeDto()));

    when(traineeService.updateTraineeTrainers(1L, trainerIds)).thenReturn(trainers);
    when(trainerMapper.toTrainerDtoList(trainers)).thenReturn(trainerDtos);

    List<TrainerDto> result = crmFacade.updateTraineeTrainers(CREDENTIALS, 1L, trainerIds);

    Assertions.assertThat(result).isSameAs(trainerDtos);
  }

  @Test
  void createTrainerShouldReturnMappedCredentials() {
    TrainerCreateDto createDto = new TrainerCreateDto(new UserCreateDto("John", "Doe"), 2L);
    Trainer trainer = createTrainer(1L, createUser(10L, "john.doe"), createTrainingType());

    when(trainerService.createTrainer(createDto)).thenReturn(trainer);
    when(trainerMapper.toCredentialsDto(trainer)).thenReturn(CREDENTIALS);

    CredentialsDto result = crmFacade.createTrainer(createDto);

    Assertions.assertThat(result).isSameAs(CREDENTIALS);
  }

  @Test
  void updateTrainerShouldReturnMappedDto() {
    Long trainerId = 1L;
    TrainerUpdateDto updateDto = new TrainerUpdateDto(2L);
    Trainer trainer = createTrainer(trainerId, createUser(10L, "john.doe"), createTrainingType());
    TrainerDto trainerDto = new TrainerDto(trainerId, 10L, "john.doe", createTrainingTypeDto());

    when(trainerService.updateTrainer(trainerId, updateDto)).thenReturn(trainer);
    when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

    TrainerDto result = crmFacade.updateTrainer(CREDENTIALS, trainerId, updateDto);

    Assertions.assertThat(result).isSameAs(trainerDto);
  }

  @Test
  void selectTrainerShouldReturnMappedDto() {
    Trainer trainer = createTrainer(1L, createUser(10L, "john.doe"), createTrainingType());
    TrainerDto trainerDto = new TrainerDto(1L, 10L, "john.doe", createTrainingTypeDto());

    when(trainerService.getTrainer(1L)).thenReturn(trainer);
    when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

    TrainerDto result = crmFacade.selectTrainer(CREDENTIALS, 1L);

    Assertions.assertThat(result).isSameAs(trainerDto);
  }

  @Test
  void selectTrainerByUsernameShouldReturnMappedDto() {
    Trainer trainer = createTrainer(1L, createUser(10L, "john.doe"), createTrainingType());
    TrainerDto trainerDto = new TrainerDto(1L, 10L, "john.doe", createTrainingTypeDto());

    when(trainerService.getTrainerByUsername("john.doe")).thenReturn(trainer);
    when(trainerMapper.toTrainerDto(trainer)).thenReturn(trainerDto);

    TrainerDto result = crmFacade.selectTrainerByUsername(CREDENTIALS, "john.doe");

    Assertions.assertThat(result).isSameAs(trainerDto);
  }

  @Test
  void selectAllTrainersShouldReturnMappedDtos() {
    List<Trainer> trainers =
        List.of(createTrainer(1L, createUser(10L, "john.doe"), createTrainingType()));
    List<TrainerDto> trainerDtos =
        List.of(new TrainerDto(1L, 10L, "john.doe", createTrainingTypeDto()));

    when(trainerService.getAllTrainers()).thenReturn(trainers);
    when(trainerMapper.toTrainerDtoList(trainers)).thenReturn(trainerDtos);

    List<TrainerDto> result = crmFacade.selectAllTrainers(CREDENTIALS);

    Assertions.assertThat(result).isSameAs(trainerDtos);
  }

  @Test
  void selectTrainersNotAssignedToTraineeShouldReturnMappedDtos() {
    List<Trainer> trainers =
        List.of(createTrainer(1L, createUser(10L, "john.doe"), createTrainingType()));
    List<TrainerDto> trainerDtos =
        List.of(new TrainerDto(1L, 10L, "john.doe", createTrainingTypeDto()));

    when(trainerService.getTrainersNotAssignedToTrainee("trainee.username")).thenReturn(trainers);
    when(trainerMapper.toTrainerDtoList(trainers)).thenReturn(trainerDtos);

    List<TrainerDto> result =
        crmFacade.selectTrainersNotAssignedToTrainee(CREDENTIALS, "trainee.username");

    Assertions.assertThat(result).isSameAs(trainerDtos);
  }

  @Test
  void createTrainingShouldReturnMappedDto() {
    TrainingCreateDto createDto = createTrainingCreateDto();
    Training training = createTraining();
    TrainingDto trainingDto = createTrainingDto();

    when(trainingService.createTraining(createDto)).thenReturn(training);
    when(trainingMapper.getTrainingDto(training)).thenReturn(trainingDto);

    TrainingDto result = crmFacade.createTraining(CREDENTIALS, createDto);

    Assertions.assertThat(result).isSameAs(trainingDto);
  }

  @Test
  void getTrainingShouldReturnMappedDto() {
    Training training = createTraining();
    TrainingDto trainingDto = createTrainingDto();

    when(trainingService.getTraining(1L)).thenReturn(training);
    when(trainingMapper.getTrainingDto(training)).thenReturn(trainingDto);

    TrainingDto result = crmFacade.getTraining(CREDENTIALS, 1L);

    Assertions.assertThat(result).isSameAs(trainingDto);
  }

  @Test
  void getAllTrainingsShouldReturnMappedDtos() {
    Training firstTraining = createTraining();
    Training secondTraining = createTraining();
    secondTraining.setId(2L);
    TrainingDto firstDto = createTrainingDto();
    TrainingDto secondDto =
        new TrainingDto(2L, "Spring Basics", createTrainingTypeDto(), null, null, null, 90);

    when(trainingService.getAllTrainings()).thenReturn(List.of(firstTraining, secondTraining));
    when(trainingMapper.getTrainingDto(firstTraining)).thenReturn(firstDto);
    when(trainingMapper.getTrainingDto(secondTraining)).thenReturn(secondDto);

    List<TrainingDto> result = crmFacade.getAllTrainings(CREDENTIALS);

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  @Test
  void getTraineeTrainingsShouldReturnMappedDtos() {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(null, null, null, null);
    Training training = createTraining();
    TrainingDto trainingDto = createTrainingDto();

    when(trainingService.getTraineeTrainings("john.doe", criteria)).thenReturn(List.of(training));
    when(trainingMapper.getTrainingDto(training)).thenReturn(trainingDto);

    List<TrainingDto> result = crmFacade.getTraineeTrainings(CREDENTIALS, "john.doe", criteria);

    Assertions.assertThat(result).containsExactly(trainingDto);
  }

  @Test
  void getTrainerTrainingsShouldReturnMappedDtos() {
    TrainerTrainingsCriteriaDto criteria = new TrainerTrainingsCriteriaDto(null, null, null);
    Training training = createTraining();
    TrainingDto trainingDto = createTrainingDto();

    when(trainingService.getTrainerTrainings("john.doe", criteria)).thenReturn(List.of(training));
    when(trainingMapper.getTrainingDto(training)).thenReturn(trainingDto);

    List<TrainingDto> result = crmFacade.getTrainerTrainings(CREDENTIALS, "john.doe", criteria);

    Assertions.assertThat(result).containsExactly(trainingDto);
  }

  private User createUser(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setPassword("password");
    return user;
  }

  private Trainee createTrainee(Long id, User user) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setUser(user);
    return trainee;
  }

  private Trainer createTrainer(Long id, User user, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
    trainer.setUser(user);
    trainer.setSpecialization(specialization);
    return trainer;
  }

  private Training createTraining() {
    Training training = new Training();
    training.setId(1L);
    training.setName("Java Basics");
    training.setType(createTrainingType());
    training.setScheduledDate(LocalDate.of(2026, 5, 8));
    training.setDurationInMinutes(60);
    return training;
  }

  private TrainingType createTrainingType() {
    TrainingType type = new TrainingType();
    type.setId(2L);
    type.setName("Fitness");
    return type;
  }

  private TrainingTypeDto createTrainingTypeDto() {
    return new TrainingTypeDto(2L, "Fitness");
  }

  private TraineeCreateDto createTraineeCreateDto() {
    return new TraineeCreateDto(new UserCreateDto("John", "Doe"), LocalDate.of(1990, 1, 1), null);
  }

  private TrainingCreateDto createTrainingCreateDto() {
    return new TrainingCreateDto("Java Basics", 2L, 3L, 4L, LocalDate.of(2026, 5, 8), 60);
  }

  private TrainingDto createTrainingDto() {
    return new TrainingDto(
        1L,
        "Java Basics",
        createTrainingTypeDto(),
        null,
        null,
        LocalDate.of(2026, 5, 8),
        60);
  }
}
